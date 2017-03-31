import java.util.*;

/* A reader */
public class BSAReader implements Reader{
    BSATag[] tags;
    int idLength;

    TreeSet<Integer> currentCollisionBits;     //All bitindexes that differ from query
    TreeSet<Integer> currentNonCollisionBits;  //Used to check if more bits collide
    
    char[] responseToCompare;   
    int tagsFound = 0;

    public BSAReader(BSATag[] tags, int numberOfBitsInId){
        this.tags = tags;
        idLength = numberOfBitsInId;
        responseToCompare = new char[idLength];
    }

    /* Uses algorithm to identify all tags */
    private void identifyTags(){
        char[] query = new char[numberOfBitsInId];
        Arrays.fill(query,'1');
        while(tagsFound != tags.length){    //While tags still responding
            while(sendQuery(query) > 1){    
                query = findNewQuery(query);
            }
            tagsFound++;
        }

    }

    /* Uses a previous query and biggest collision bit to find new query */
    /* Fills the next query with the same bits as previous query up until
    * the first collision bit which will be set to 0 and the rest to 1*/
    private char[] findNewQuery(query){
        int firstCollisionBit = currentCollisionBits.first();
        for(int i = 0; i < firstCollisionBit; i++){
            query[i] = responseToCompare[i];
        }
        query[firstCollisionBit] = '0';
        for(int i = firstCollisionBit+1; i < idLength; i++){
            query[i] = '1';
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
        int numberOfReturns = 0;
        char[] response;
        for(int i = 0; i < tags.length; i++){
            response = tags[i].respondBSAQuery(query);
            if(response != null){
                if(i == 0){
                    responseToCompare = response;
                }else{
                    checkCollisionBits(response);
                }
                numberOfReturns++;
            }
        }
        return numberOfReturns;
    }

    /* Check from the current non collision bits which might collide */
    /* First response (responseToCompare) from a tag will be compared to all other ids.
    Note that this works because they are strings of binaries which means that
    if two bits at a position differs from eachother, then one of them also
    differs from the response we compare with.
    */
    private void checkCollisionBits(char[] response){
        Iterator it = currentNonCollisionBits.iterator();
        while(it.hasNext()){
            Integer i = (Integer) it.next();
            if(responseToCompare[i] != response[i]){        
                currentCollisionBits.add(i);    //Bit collision
                currentNonCollisionBits.remove(i);
            }
        }
    }
}
