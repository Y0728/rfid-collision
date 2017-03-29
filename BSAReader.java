import java.util.*;

/* A reader */
public class BSAReader implements Reader{
    BSATag[] tags;
    int idLength;

    TreeSet<Integer> currentCollisionBits;     //All bitindexes that differ from query
    TreeSet<Integer> currentNonCollisionBits;  //Used to check if more bits collide
    
    /* First response from a tag will be compared to all other ids.
    Note that this works because they are strings of binaries which means that
    if two bits at a position differs from eachother, then one of them also
    differs from the response we compare with.
    */
    char[] responseToCompare;   

    public BSAReader(BSATag[] tags, int numberOfBitsInId){
        this.tags = tags;
        idLength = numberOfBitsInId;
        responseToCompare = new char[idLength];
    }

    /* Uses algorithm to identify all tags */
    private void identifyTags(){

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
    public void sendQuery(char[] query){
        char[] response;
        for(int i = 0; i < tags.length; i++){
            response = tags[i].respondBSAQuery(query);
            if(response != null){
                if(i == 0){
                    responseToCompare = response;
                }else{
                    checkCollisionBits(response);
                }
            }
        }
    }

    /* Check from the current non collision bits which might collide */
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
