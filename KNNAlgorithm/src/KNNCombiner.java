import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class KNNCombiner extends Reducer<Text, Vector2SF, Text, Vector2SF> {
        protected void reduce(
                        Text key,
                        java.lang.Iterable<Vector2SF> value,
                        org.apache.hadoop.mapreduce.Reducer<Text, Vector2SF, Text, Vector2SF>.Context context)
                        throws java.io.IOException, InterruptedException {
                ArrayList<Vector2SF> vs = new ArrayList<Vector2SF>();
                // sort each vector2SF by similarty
                for (Vector2SF v : value) {
                        vs.add(new Vector2SF(v.getV1(), v.getV2(), v.getV3()));
                }
                Collections.sort(vs, new Comparator<Vector2SF>() {
                        @Override
                        public int compare(Vector2SF o1, Vector2SF o2) {
                                return Float.compare(o1.getV2(), o2.getV2());
                        }
                });
                int k = 5;
                for (int i = 0; i < k && i < vs.size(); i++) {
                        context.write(key, vs.get(i));
                }
        };
}