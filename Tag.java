
/* A tag that responds to queries from the reader */
public class Tag{
    private boolean active = true;
    char[] id;

    public Tag(int size, long idNumber){
        createId(size,idNumber);
    }

    /* Sets up the id from a given long
     * Will prepend 0s if the number of bits doesn't match*/
    public void createId(int size, long idNumber){
        /* We need to add 0s to the start of id if the idNumber doesn't
         * fill the id array */
        id = new char[size];
        StringBuilder s = new StringBuilder(Long.toBinaryString(idNumber));
        int count = 0;
        for(int i = 0; i < id.length; i++){
            if(i >= size - s.length()){
                id[i] = s.charAt(count);
                count++;
            }else{
                id[i] = '0';
            }
        }
    }

    /* Responds a dynamic bsa call which is only the part of the id
    * which is the part of the id that complements the query if the
    * query would make up the top bits in the id.
    * Example: query(101) id([101]001) -> reponse(001)*/
    public char[] respondDBSAQuery(char[] dynQuery){
        if(dynQuery.length != id.length){
            char[] bsaQuery = new char[id.length];
            for(int i = 0; i < bsaQuery.length; i++){
                bsaQuery[i] = i < dynQuery.length ? dynQuery[i] : '1';
            }
            char[] bsaResponse = respondBSAQuery(bsaQuery);
            if(bsaResponse == null){
                return null;
            }
            char[] dynResponse = new char[id.length - dynQuery.length];
            for(int i = 0; i < dynResponse.length; i++){
                dynResponse[i] = bsaResponse[dynQuery.length + i];
            }
            return dynResponse;
        }else{
            return respondBSAQuery(dynQuery);
        }
    }

    /* Returns with whole id if the id is equal or less than id
     * returns null if not
     * If tag is inactive it won't respond*/
    public char[] respondBSAQuery(char[] query){
        if(!active){
            return null;
        }
        if(query.length != id.length){
            System.err.println("Size of tag: " + id.length + " does not match " + query.length);
            return null;
        }
        int size = id.length;

        for(int i = 0; i < size; i++){
            //Check if it doesn't match
            if(id[i] != query[i]){
                // 0 < 1 even in ascii
                if(id[i] < query[i]){
                    return id;
                }else{
                    return null;
                }
            }
        }
        //All matched
        return id;
    }

    @Override
    public String toString(){
        return String.valueOf(id);
    }

    public void activate(){
        active = true;
    }

    public void deactivate(){
        active = false;
    }

    public boolean isActive(){
        return active;
    }
}
