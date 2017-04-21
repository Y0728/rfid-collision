import java.util.*;

/* A reader using the Jump and Dynamic algorithm */
public class JumpAndDynamicReader extends Reader{

    TreeSet<Integer> backTrackBits;

    public JumpAndDynamicReader(Tag[] tags, int numberOfBitsInId){
        this.tags = tags;
        idLength = numberOfBitsInId;
        responseToCompare = new char[idLength];
        backTrackBits = new TreeSet<Integer>();
    }

    /* Uses the Jump and Dynamic algorithm to identify all tags.
    * Using the backtrack method by not return to the very first query
    * that contains only ones. It instead jumps back to collision bits
    * where it still has not checked with all values. It also uses the
    * Dynamic BSA to not send whole queries and receive whole responses.*/
    public void identifyTags(){
        char[] query = new char[idLength];
        resetCollisionBits();
        while(tagsFound != tags.length){    //While tags still responding
            query = backTrack(query);         //Jump back to next branch
            while(sendQuery(query) > 1){    //While more than one tag responds
                query = findNewQuery(query);    //Update query according to collisions
                resetCollisionBits();
            }
            //System.out.println("Tag identified: " + tagsFound + " - " + lastRespondingTag.toString());
            lastRespondingTag.deactivate();
            tagsFound++;
        }
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
        calculateResults();
    }

    /*
    */
    public char[] backTrack(char[] query){
        if(backTrackBits.size() == 0){
            char[] newQuery = new char[idLength];
            Arrays.fill(newQuery, '1');
            return newQuery;
        }
        int lastBackTrack = backTrackBits.pollLast();
        char[] newQuery = new char[lastBackTrack+1];
        for(int i = 0; i < lastBackTrack; i++){
            newQuery[i] = query[i];
        }
        newQuery[lastBackTrack] = '1';
        return newQuery;
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
                //First query will return full length response
                newQuery[i] = responseToCompare[i];
            }else if(i >= query.length){
                //Fill new query with the non collision bits up until.
                newQuery[i] = responseToCompare[i - query.length];
            }else{
                //Start of query will the same as before
                newQuery[i] = query[i];
            }
        }
        newQuery[newQuery.length-1] = '0';
        backTrackBits.add(newQuery.length - 1);

        return newQuery;
    }


    /* Returns the appropriate response from the tag depending
    * on which algorith is used */
    public char[] getResponseFromTag(Tag tag, char[] query){
        return tag.respondDBSAQuery(query);
    }

    /* Check from the current non collision bits which might collide */
    /* First response (responseToCompare) from a tag will be compared to all other ids.
    Note that this works because they are strings of binaries which means that
    if two bits at one position index differs between two responses, then one of them also
    differs from the response we compare with.
    */
    public void checkCollisionBits(char[] response){
        int querySize = idLength-response.length;
        for(int i = 0; i < response.length; i++){
            if(responseToCompare[i] != response[i]){
                currentCollisionBits.add(querySize+i);
                currentNonCollisionBits.remove(querySize+i);
            }
        }
    }

}
