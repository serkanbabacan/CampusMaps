import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;

public class TestPrefixSample {
    static class IO {
        @JsonProperty("input")
        String input;

        @JsonProperty("output")
        Object output;
    }

    public static void main(String[] args) {
        GraphDB graph = new GraphDB("berkeley.osm");
        ObjectMapper mapper = new ObjectMapper();
        Gson gson = new Gson();
        try {
            IO[] entries = mapper.readValue(new File("prefix_sample.json"), IO[].class);
            for (IO io : entries) {
                List<Vertex> test = graph.tst.valuesWithPrefix(io.input);
                test.sort(Comparator.comparing(Vertex::getName));
                List<Vertex> output = Arrays.asList(gson.fromJson(gson.toJson(io.output), Vertex[].class));
                output.sort(Comparator.comparing(Vertex::getName));

               if (!test.equals(output)) {
                   System.out.println("Expected: '" + gson.toJson(io.output) + "'\nGot: '" + gson.toJson(test) + "'");
                   System.out.println("For: '" + io.input + "'\n");
                   TestUtils.fail();
               }
            }
            TestUtils.pass();
        } catch (IOException e) {
            e.printStackTrace();
            TestUtils.fail();
        }
    }
}
