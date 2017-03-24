
/*  */
public class BSATag{
    char[] id;

    public BSATag(int size, long idNumber){
        createId(size,idNumber);
        System.out.println(id);
    }

    /* Sets up the id */
    public void createId(int size ,long idNumber){

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
}
