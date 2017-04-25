import java.util.*;

/* A reader using the Dynamic Binary Search Algorithm */
public class DBSAReader extends Reader{


    public DBSAReader(Tag[] tags, int numberOfBitsInId){
        this.tags = tags;
        idLength = numberOfBitsInId;
        responseToCompare = new char[idLength];
    }

    /* Uses the dynamic binary search algorithm to identify all tags
    * First query and response is the same length of all tags. Depending on
    * the collision bits in responses the reader sends only the necessary
    * part of the query and recieves only the complenting necessary part
    * from the tags */
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
