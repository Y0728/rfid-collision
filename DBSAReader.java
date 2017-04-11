import java.util.*;

/* A reader */
public class DBSAReader implements Reader{
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

    public DBSAReader(BSATag[] tags, int numberOfBitsInId){
        this.tags = tags;
        idLength = numberOfBitsInId;
        responseToCompare = new char[idLength];
    }

    /* Uses algorithm to identify all tags */
    public void identifyTags(){
        char[] query = new char[idLength];
        resetCollisionBits();
        while(tagsFound != tags.length){    //While tags still responding
            query = new char[idLength];
            Arrays.fill(query,'1');         //Reset query
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
    * the first collision bit which will be set to 0 and returns it.
    * Dynamic BSA does not need the query of the same length as id*/
    private char[] findNewQuery(char[] query){
        if(currentCollisionBits.size() < 1){
            System.out.println("No collision bits when searching for new query");
            return null;
        }
        char[] newQuery = new char[currentCollisionBits.first() + 1];
        for(int i = 0; i < newQuery.length-1; i++){ //up until the last index
            if(responseToCompare.length == idLength){ 
                //First query to scope the next query  
                newQuery[i] = responseToCompare[i];
            }else if(i >= query.length){
                newQuery[i] = responseToCompare[i - query.length];
            }else{
                newQuery[i] = query[i];
            }
        }        
        newQuery[newQuery.length-1] = '0';

        return newQuery;
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
            response = tags[i].respondDBSAQuery(query);
            if(response != null){       //null if no response
                System.out.println(">\t" + String.valueOf(response));
                //System.out.println("RESPONSE: " + tags[i]);
                lastRespondingTag = tags[i];
                if(firstResponse){       
                    //First response used to compare
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
        int querySize = idLength-response.length;
        for(int i = 0; i < response.length; i++){
            if(responseToCompare[i] != response[i]){
                currentCollisionBits.add(querySize+i);
                currentNonCollisionBits.remove(querySize+i);
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
