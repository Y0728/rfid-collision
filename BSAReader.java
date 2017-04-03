import java.util.*;

/* A reader */
public class BSAReader implements Reader{
    BSATag[] tags;
    int idLength;

    TreeSet<Integer> currentCollisionBits;     //All bitindexes that differ from query
    TreeSet<Integer> currentNonCollisionBits;  //Used to check if more bits collide

    char[] responseToCompare;
    int tagsFound = 0;
    BSATag lastRespondingTag;

    public BSAReader(BSATag[] tags, int numberOfBitsInId){
        this.tags = tags;
        idLength = numberOfBitsInId;
        responseToCompare = new char[idLength];
    }

    /* Uses algorithm to identify all tags */
    public void identifyTags(){
        char[] query = new char[idLength];
        resetCollisionBits();
        while(tagsFound != tags.length){    //While tags still responding
            Arrays.fill(query,'1');
            while(sendQuery(query) > 1){
                query = findNewQuery(query);
                resetCollisionBits();
            }
            System.out.println("Tag identified: " + tagsFound + ": " + lastRespondingTag.toString());
            lastRespondingTag.deactivate();
            tagsFound++;
        }
    }

    /* Uses a previous query and biggest collision bit to find new query */
    /* Fills the next query with the same bits as previous query up until
    * the first collision bit which will be set to 0 and the rest to 1*/
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
        boolean firstResponse = true;
        int numberOfReturns = 0;
        char[] response;
        for(int i = 0; i < tags.length; i++){
            response = tags[i].respondBSAQuery(query);
            if(response != null){
                //System.out.println("RESPONSE: " + tags[i]);
                lastRespondingTag = tags[i];
                if(firstResponse){     //First response used to compare
                    responseToCompare = response;
                    firstResponse = false;
                }else{          //The rest used to ACTIVE TAGSfind collision
                    checkCollisionBits(response);
                }
                numberOfReturns++;
            }
        }
        //System.out.println("COLLBITS: " + currentCollisionBits);
        //System.out.println("QUERY: " + String.valueOf(query));
        return numberOfReturns;
    }

    /* Check from the current non collision bits which might collide */
    /* First response (responseToCompare) from a tag will be compared to all other ids.
    Note that this works because they are strings of binaries which means that
    if two bits at a position differs from eachother, then one of them also
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
