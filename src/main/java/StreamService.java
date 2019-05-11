import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamService {

    public static void main(String [] args){
        List<String> dataSt = new ArrayList<>();
        dataSt.add("Aku");
        dataSt.add("Kamu");
        dataSt.add("Kalian");

        for (String n : listToStream(dataSt)){
            System.out.println(n);
        }
    }

    public static <T> List<T> listToStream(List<T> listItem){
        return (List<T>) listItem.stream().map(i -> i + " - Mapping").collect(Collectors.toList());
    }
}
