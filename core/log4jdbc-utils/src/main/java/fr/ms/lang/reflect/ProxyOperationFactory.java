package fr.ms.lang.reflect;

import java.lang.reflect.Method;

public interface ProxyOperationFactory {

    ProxyOperation newOperation(TimeInvocation timeInvocation, Object proxy, Method method, Object[] args);
}
