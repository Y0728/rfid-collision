

public class BSATag{
    char[] id;

    public BSATag(int size, long idNumber){
        createId(idNumber);
    }

    public void createId(long idNumber){
        String s = Long.toBinaryString(idNumber);
        id = s.toCharArray();
        System.out.println(s);
    }
}
