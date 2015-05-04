package biz.paluch.spinach.output;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import biz.paluch.spinach.Job;

import com.lambdaworks.redis.codec.RedisCodec;
import com.lambdaworks.redis.protocol.CommandOutput;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class JobListOutput<K, V> extends CommandOutput<K, V, List<Job<K, V>>> {

    private K defaultQueue;
    private K queue;
    private String id;
    private V body;

    public JobListOutput(RedisCodec<K, V> codec) {
        super(codec, new ArrayList<Job<K, V>>());
    }

    public JobListOutput(RedisCodec<K, V> codec, K defaultQueue) {
        super(codec, new ArrayList<Job<K, V>>());
        this.defaultQueue = defaultQueue;
    }

    @Override
    public void set(ByteBuffer bytes) {

        if (queue == null) {
            if (defaultQueue != null) {
                queue = defaultQueue;
            } else {
                queue = codec.decodeKey(bytes);
                return;
            }
        }

        if (id == null) {
            id = decodeAscii(bytes);
            return;
        }

        if (body == null) {
            body = codec.decodeValue(bytes);
            return;
        }
    }

    @Override
    public void complete(int depth) {

        if (id != null && body != null) {
            output.add(new Job<K, V>(queue, id, body));

            queue = null;
            id = null;
            body = null;
        }

    }
}
