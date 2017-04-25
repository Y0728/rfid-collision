import java.util.*;

/* A reader using the regular Binary Search Algorithm */
public class BSAReader extends Reader{


    public BSAReader(Tag[] tags, int numberOfBitsInId){
        this.tags = tags;
        idLength = numberOfBitsInId;
        responseToCompare = new char[idLength];
    }

    /* Uses the binary search algorithm to identify
    * the tags in the tag array. First query will be a bunch of '1's
    * An depending on collision bits in the responses the next query is formed
    * When only one tag responds the tag is identified */
    public void identifyTags(){
        char[] query = new char[idLength];
        resetCollisionBits();
        while(tagsFound != tags.length){    //While tags still responding
            Arrays.fill(query,'1');         //Reset query
            while(sendQuery(query) > 1){    //While more than one tag responds
                query = findNewQuery(query);    //Update query according to collisions
                resetCollisionBits();
            }
            //System.out.println("Tag identified: " + tagsFound + " - " + lastRespondingTag.toString());
            lastRespondingTag.deactivate();
            tagsFound++;
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
