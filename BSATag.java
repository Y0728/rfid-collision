
/*  */
public class BSATag{
    private boolean active = true;
    char[] id;

    public BSATag(int size, long idNumber){
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

    /* Returns with whole id if the id is equal or less than id
     * returns null if not
     * If tag is inactive it won't respond*/
    public char[] respondBSAQuery(char[] query){
        if(!active){
            return null;
        }
        if(query.length != id.length){
            System.err.println("Size of BSAtag: " + id + " does not match " + query);
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
