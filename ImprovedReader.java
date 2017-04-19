import java.util.*;

/* A reader */
public class ImprovedReader implements Reader{
    BSATag[] tags;
    int idLength;

    TreeSet<Integer> currentCollisionBits;     //All bitindexes that differ from query
    TreeSet<Integer> currentNonCollisionBits;  //Used to check if more bits collide

    //Algorithm helping variables
    char[] responseToCompare;
    int tagsFound = 0;
    BSATag lastRespondingTag;

    //Result helping variables
    long currentNumberOfBits = 0;
    long currentNumberOfQueries = 0;

    // RESULTING DATA
    double averageNumberOfQueries = 0;
    double averageNumberOfBits = 0;

    public ImprovedReader(BSATag[] tags, int numberOfBitsInId){
        this.tags = tags;
        idLength = numberOfBitsInId;
        responseToCompare = new char[idLength];
    }

    /* Uses algorithm to identify all tags */
    public void identifyTags(){
        char[] query = new char[idLength];
        Arrays.fill(query,'1');         //Reset query
        queryRepeater(query);

        calculateResults();
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

    /* Average number of queries sent to identify one tag */
    public double getQueryAverage(){
        return averageNumberOfQueries;
    }

    /* Average number of bits required to identify one tag */
    public double getBitAverage(){
        return averageNumberOfBits;
    }

    /* Calculating the average result*/
    private void calculateResults(){
        System.out.println("Total Queries: " + currentNumberOfQueries);
        System.out.println("Total Bits: " + currentNumberOfBits);
        averageNumberOfBits = currentNumberOfBits / ((double)tags.length);
        averageNumberOfQueries = currentNumberOfQueries / ((double)tags.length);
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

    /* Resets or initiates collision bit sets*/
    private void resetCollisionBits(){
        currentCollisionBits = new TreeSet<Integer>();
        currentNonCollisionBits = new TreeSet<Integer>();
        for(int i = 0; i < idLength; i++){
            currentNonCollisionBits.add(i);
        }
    }

    /* Sends the query to the cloud of tags */
    public int sendQuery(char[] query){
        currentNumberOfBits += query.length;
        currentNumberOfQueries++;      //One query has succesfully been sent
        System.out.println("Q " + currentNumberOfQueries + ": \t" + String.valueOf(query));

        boolean firstResponse = true;
        int numberOfReturns = 0;
        char[] response;
        for(int i = 0; i < tags.length; i++){
            response = tags[i].respondBSAQuery(query);
            if(response != null){       //null if no response
                System.out.println(">\t" + String.valueOf(response));
                //System.out.println("RESPONSE: " + tags[i]);
                lastRespondingTag = tags[i];
                if(firstResponse){       //First response used to compare
                    responseToCompare = response;
                    firstResponse = false;
                }else{
                    //The rest used to ACTIVE TAGSfind collision
                    checkCollisionBits(response);
                }
                numberOfReturns++;
                currentNumberOfBits += response.length;    //The bits returned
            }
        }
        //System.out.println("COLLBITS: " + currentCollisionBits);
        //System.out.println("QUERY: " + String.valueOf(query));


        return numberOfReturns;
    }

    /* Check from the current non collision bits which might collide */
    /* First response (responseToCompare) from a tag will be compared to all other ids.
    Note that this works because they are strings of binaries which means that
    if two bits at one position index differs between two responses, then one of them also
    differs from the response we compare with.
    */
    private void checkCollisionBits(char[] response){
        Iterator<Integer> it = currentNonCollisionBits.iterator();
        while(it.hasNext()){
            int i = it.next();
            if(responseToCompare[i] != response[i]){
                currentCollisionBits.add(i);    //Bit collision
                it.remove();
            }
        }
    }

    private void printActiveTags(){
        System.out.println("_______ACTIVE TAGS___________");
        for(int i = 0; i < tags.length; i++){
            if(tags[i].isActive()){
                System.out.println(tags[i]);
            }
        }
        System.out.println("______________________________");

    }
}
