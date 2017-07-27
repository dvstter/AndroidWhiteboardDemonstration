// File Creator : Hmh

import java.util.Stack;

// 实现一个定长的栈
public class SizedStack<T> extends Stack<T> {
    private final int maxSize; // 栈的最大长度
    public SizedStack(int size) {
        super();
        this.maxSize = size;
    }
    
    // 重写push方法
    @Override
    public Object push(Object object) {
        while (this.size()>maxSize) {
            this.remove(0);
        }
        return super.push((T)object);
    }
}
