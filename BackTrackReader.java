import java.util.*;

/* A reader using the jumpback algorithm to identify tags*/
public class BackTrackReader implements Reader{
    BSATag[] tags;
    int idLength;

    TreeSet<Integer> currentCollisionBits;     //All bitindexes that differ from query
    TreeSet<Integer> currentNonCollisionBits;  //Used to check if more bits collide
    TreeSet<Integer> backTrackBits;            //Bit indexes where branches start

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

    public BackTrackReader(BSATag[] tags, int numberOfBitsInId){
        this.tags = tags;
        idLength = numberOfBitsInId;
        responseToCompare = new char[idLength];
        backTrackBits = new TreeSet<Integer>();
    }

    /* Uses algorithm to identify all tags */
    public void identifyTags(){
        char[] query = new char[idLength];
        resetCollisionBits();
        while(tagsFound != tags.length){    //While tags still responding
            query = backTrack(query);         //Jump back to last branch
            while(sendQuery(query) > 1){    //While more than one tag responds
                query = findNewQuery(query);    //Update query according to collisions
                resetCollisionBits();
            }
            //System.out.println("Tag identified: " + tagsFound + " - " + lastRespondingTag.toString());
            lastRespondingTag.deactivate();
            tagsFound++;
        }

        calculateResults();
    }

    /* Returns a new query using backtracking to jump back to 
    * a branch in the search tree which has not been exhausted.
    * This is done by keeping track of the bit position of where the 
    * branches start and changing the bit where the branch started. 
    * Branches start with a 0, so 1 is placed at the branch start in order
    * to exhaust the other branch. Algorithm correctness is guaranteed by
    * also changing the rest of the branch bits to 1 for all tags to respond.
    * */
    public char[] backTrack(char[] query){
        char[] newQuery = query;
        if(backTrackBits.size() == 0){
            Arrays.fill(newQuery, '1');
        }else{
            int lastBackTrack = backTrackBits.pollLast();
            for(int i = lastBackTrack; i < newQuery.length; i++){
                newQuery[i] = '1';
            }
        }
        return newQuery;

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
                backTrackBits.add(i);
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
