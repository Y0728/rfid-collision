import java.util.*;

/* A reader using the jumpback algorithm to identify tags*/
public class BackTrackReader extends Reader{

    TreeSet<Integer> backTrackBits;            //Bit indexes where branches start

    public BackTrackReader(Tag[] tags, int numberOfBitsInId){
        this.tags = tags;
        idLength = numberOfBitsInId;
        responseToCompare = new char[idLength];
        backTrackBits = new TreeSet<Integer>();
    }

    /* Using the backtrack method by not return to the very first query
    * that contains only ones. It instead jumps back to collision bits
    * where it still has not checked with all values. */
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
