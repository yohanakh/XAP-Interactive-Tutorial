package demo;

import com.gigaspaces.client.CustomChangeOperation;
import com.gigaspaces.server.MutableServerEntry;

/**
 * Created by yohana on 8/17/14.
 */
public class MultiplyLongChangeOperation extends CustomChangeOperation {
    private static final long serialVersionUID = 1L;
    private final String path;
    private final int multiplier;

    public MultiplyLongChangeOperation(String path, int multiplier) {
        this.path = path;
        this.multiplier = multiplier;
    }

    @Override
    public String getName() {
        return "multiplyInt";
    }

    public String getPath() {
        return path;
    }

    public int getMultiplier() {
        return multiplier;
    }

    @Override
    public Object change(MutableServerEntry entry) {
        //Assume this is an integer property, if this is not true an exception will be thrown
        //and the change operation will fail
        Long oldValue = (Long)entry.getPathValue(path);
        Long newValue = oldValue.longValue() * multiplier;
        entry.setPathValue(path, newValue);
        return newValue;
    }
}