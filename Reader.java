import java.util.*;
public abstract class Reader{
    Tag[] tags;
    int idLength;

    TreeSet<Integer> currentCollisionBits;     //All bitindexes that differ from query
    TreeSet<Integer> currentNonCollisionBits;  //Used to check if more bits collide

    //Algorithm helping variables
    char[] responseToCompare;
    int tagsFound = 0;
    Tag lastRespondingTag;

    //Result helping variables
    long currentNumberOfBits = 0;
    long currentNumberOfQueries = 0;

    // RESULTING DATA
    double averageNumberOfQueries = 0;
    double averageNumberOfBits = 0;

    /* Identifies the set of tags with an RFID anti-collision algorithm */
    public abstract void identifyTags();

    /* Average number of queries sent to identify one tag */
    public double getQueryAverage(){
        return averageNumberOfQueries;
    }

    /* Average number of bits required to identify one tag */
    public double getBitAverage(){
        return averageNumberOfBits;
    }


    /* Calculating the average result*/
    protected void calculateResults(){
        System.out.println("Total number of queries: \t\t" + currentNumberOfQueries);
        System.out.println("Total number of bits: \t\t\t" + currentNumberOfBits);
        averageNumberOfBits = currentNumberOfBits / ((double)tags.length);
        averageNumberOfQueries = currentNumberOfQueries / ((double)tags.length);
        System.out.println("Average number of queries \t\t" + averageNumberOfQueries);
        System.out.println("Average number of bits: \t\t" + averageNumberOfBits);
    }


    /* Resets or initiates collision bit sets*/
    protected void resetCollisionBits(){
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
        //System.out.println("Q " + currentNumberOfQueries + ": \t" + String.valueOf(query));

        boolean firstResponse = true;
        int numberOfReturns = 0;
        char[] response;
        for(int i = 0; i < tags.length; i++){
            response = getResponseFromTag(tags[i], query);
            if(response != null){       //null if no response
                //System.out.println(">\t" + String.valueOf(response));
                //System.out.println("RESPONSE: " + tags[i]);
                lastRespondingTag = tags[i];
                if(firstResponse){
                    //First response used for comparison with other responses
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
        return numberOfReturns;
    }

    public abstract void checkCollisionBits(char[] response);

    public abstract char[] getResponseFromTag(Tag tag,char[] query);
}
