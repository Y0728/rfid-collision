import java.util.*;

/* A reader using the algorithm presented by Benssalah, Djeddou and Khelladi with
* preset loops */
public class ImprovedReader extends Reader{


    public ImprovedReader(Tag[] tags, int numberOfBitsInId){
        this.tags = tags;
        idLength = numberOfBitsInId;
        responseToCompare = new char[idLength];
    }

    /* Uses the algorithm with preset loops. When two or more collision bits
    * occur in responses, the reader creates a for loop with all possible
    * binary values in those to collision bits. This will hopefully reduce the
    * amount of tags that responds and will therefore decrease the total bits
    * flow. */
    public void identifyTags(){
        char[] query = new char[idLength];
        Arrays.fill(query,'1');         //Reset query
        queryRepeater(query);

    }

    /** Sends queries and handles the preset loops that defines the improved
     * anti collision algorithm.
     * Taking the two highest collision bits from a query and letting those
     * bits take values (0,0) (0,1) (1,0) (1,1), the lower bits after the second collision bit
     * sets as value 1 and then sending the query recursively and repeating the process
     * If there are zero or one collision bits the algorithm returns. One collision bit means
     * that we have found two tags that differ only at the collision bit index and zero collision
     * bits means that one tag is identified. If one collision bit is found the two tags will
     * be identified using regular binary search.
     */
    public void queryRepeater(char[] query){
        resetCollisionBits();
        int numberOfResults = sendQuery(query);
        if(numberOfResults == 0){
            return;
        }

        int numberOfCollisionBits = currentCollisionBits.size();
        if(numberOfCollisionBits == 0){
            lastRespondingTag.deactivate();
            tagsFound++;
            return;
        }else if(numberOfCollisionBits == 1){
            int collisionBit = currentCollisionBits.first();
            query[collisionBit] = '0';
            sendQuery(query);
            lastRespondingTag.deactivate();

            query[collisionBit] = '1';
            sendQuery(query);
            lastRespondingTag.deactivate();
            tagsFound+=2;
            return;
        }else{
            int firstCollisionBit = currentCollisionBits.pollFirst();
            int secondCollisionBit = currentCollisionBits.pollFirst();
            for(char i = 0; i <= 1; i++){
                for(char j = 0; j <= 1; j++){
                    for(int k = 0; k < secondCollisionBit; k++){
                        query[k] = responseToCompare[k];
                    }
                    query[firstCollisionBit] = (char)('0' + i); //Ascii for 0 or 1 depending on i
                    query[secondCollisionBit] = (char)('0' + j); //Ascii for 0 or 1 depending on j
                    for(int k = secondCollisionBit+1; k < query.length; k++){
                        query[k] = '1';
                    }
                    queryRepeater(query);
                }
            }
        }
    }




    /* Uses a previous query and biggest collision bit to find new query */
    /* Fills the next query with the same bits as previous query up until
    * the first collision bit which will be set to 0 and the rest of the collision
    * bits to 1 while the non collision bit stays the same as the responses*/
    private char[] findNewQuery(char[] query){
        for(int i: currentNonCollisionBits){
            query[i] = responseToCompare[i];
        }

        boolean first = true;
        for(int i: currentCollisionBits){
            if(first){
                query[i] = '0';
                first = false;
            }else{
                query[i] = '1';
            }
        }
        return query;
    }


    /* Returns the appropriate response from the tag depending
    * on which algorith is used */
    public char[] getResponseFromTag(Tag tag, char[] query){
        return tag.respondBSAQuery(query);
    }

    /* Check from the current non collision bits which might collide */
    /* First response (responseToCompare) from a tag will be compared to all other ids.
    Note that this works because they are strings of binaries which means that
    if two bits at one position index differs between two responses, then one of them also
    differs from the response we compare with.
    */
    public void checkCollisionBits(char[] response){
        Iterator<Integer> it = currentNonCollisionBits.iterator();
        while(it.hasNext()){
            int i = it.next();
            if(responseToCompare[i] != response[i]){
                currentCollisionBits.add(i);    //Bit collision
                it.remove();
            }
        }
    }

}
