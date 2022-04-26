package com.tvd12.ezyfoxserver.testing.socket;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.entity.EzyAbstractSession;
import com.tvd12.ezyfoxserver.entity.EzySession;
import com.tvd12.ezyfoxserver.socket.*;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class EzySocketRequestHandlerTest {

    @Test
    public void test() {
        ExEzySocketRequestHandler handler = new ExEzySocketRequestHandler();

        EzySocketDataHandlerGroupFetcher dataHandlerGroupFetcher = mock(EzySocketDataHandlerGroupFetcher.class);
        EzySocketDataHandlerGroup handlerGroup = mock(EzySocketDataHandlerGroup.class);
        when(dataHandlerGroupFetcher.getDataHandlerGroup(any(EzySession.class))).thenReturn(handlerGroup);

        EzySession session = spy(EzyAbstractSession.class);
        session.setActivated(true);
        EzyRequestQueue requestQueue = new EzyNonBlockingRequestQueue();
        when(session.getExtensionRequestQueue()).thenReturn(requestQueue);
        EzyArray array = EzyEntityFactory.newArrayBuilder()
            .append(10)
            .build();
        EzySocketRequest request = new EzySimpleSocketRequest(session, array);
        EzySessionTicketsRequestQueues sessionTicketsRequestQueues = new EzySessionTicketsRequestQueues();
        handler.setSessionTicketsQueue(sessionTicketsRequestQueues.getExtensionQueue());
        sessionTicketsRequestQueues.addRequest(request);

        handler.setDataHandlerGroupFetcher(dataHandlerGroupFetcher);
        handler.handleEvent();
        handler.destroy();
    }

    @Test
    public void handleGroupNullCaseTest() {
        ExEzySocketRequestHandler handler = new ExEzySocketRequestHandler();

        EzySocketDataHandlerGroupFetcher dataHandlerGroupFetcher = mock(EzySocketDataHandlerGroupFetcher.class);
        when(dataHandlerGroupFetcher.getDataHandlerGroup(any(EzySession.class))).thenReturn(null);

        EzySession session = spy(EzyAbstractSession.class);
        when(session.isActivated()).thenReturn(Boolean.TRUE);
        EzyRequestQueue requestQueue = new EzyNonBlockingRequestQueue();
        when(session.getExtensionRequestQueue()).thenReturn(requestQueue);
        EzyArray array = EzyEntityFactory.newArrayBuilder()
            .append(10)
            .build();
        EzySocketRequest request = new EzySimpleSocketRequest(session, array);
        EzySessionTicketsRequestQueues sessionTicketsRequestQueues = new EzySessionTicketsRequestQueues();
        handler.setSessionTicketsQueue(sessionTicketsRequestQueues.getExtensionQueue());
        sessionTicketsRequestQueues.addRequest(request);

        handler.setDataHandlerGroupFetcher(dataHandlerGroupFetcher);
        handler.handleEvent();
    }

    @Test
    public void processRequestQueue0ExceptionCaseTest() {
        ExEzySocketRequestHandler handler = new ExEzySocketRequestHandler();

        EzySocketDataHandlerGroupFetcher dataHandlerGroupFetcher = mock(EzySocketDataHandlerGroupFetcher.class);
        when(dataHandlerGroupFetcher.getDataHandlerGroup(any(EzySession.class))).thenThrow(new IllegalArgumentException());

        EzySession session = spy(EzyAbstractSession.class);
        when(session.isActivated()).thenReturn(Boolean.TRUE);
        EzyRequestQueue requestQueue = new EzyNonBlockingRequestQueue();
        when(session.getExtensionRequestQueue()).thenReturn(requestQueue);
        EzyArray array = EzyEntityFactory.newArrayBuilder()
            .append(10)
            .build();
        EzySocketRequest request = new EzySimpleSocketRequest(session, array);
        EzySessionTicketsRequestQueues sessionTicketsRequestQueues = new EzySessionTicketsRequestQueues();
        handler.setSessionTicketsQueue(sessionTicketsRequestQueues.getExtensionQueue());
        sessionTicketsRequestQueues.addRequest(request);

        handler.setDataHandlerGroupFetcher(dataHandlerGroupFetcher);
        handler.handleEvent();
    }

    @Test
    public void processRequestQueue0InterruptTest() throws Exception {
        ExEzySocketRequestHandler handler = new ExEzySocketRequestHandler();

        EzySocketDataHandlerGroupFetcher dataHandlerGroupFetcher = mock(EzySocketDataHandlerGroupFetcher.class);
        when(dataHandlerGroupFetcher.getDataHandlerGroup(any(EzySession.class))).thenThrow(new IllegalArgumentException());

        EzySessionTicketsRequestQueues sessionTicketsRequestQueues = new EzySessionTicketsRequestQueues();
        handler.setSessionTicketsQueue(sessionTicketsRequestQueues.getExtensionQueue());
        handler.setDataHandlerGroupFetcher(dataHandlerGroupFetcher);

        Thread thread = new Thread(handler::handleEvent);
        thread.start();
        Thread.sleep(100L);
        thread.interrupt();
    }

    @Test
    public void processRequestQueue0RemainTest() {
        // given
        ExEzySocketRequestHandler handler = new ExEzySocketRequestHandler();

        EzySocketDataHandlerGroup handlerGroup = mock(EzySocketDataHandlerGroup.class);
        EzySocketDataHandlerGroupFetcher dataHandlerGroupFetcher = mock(EzySocketDataHandlerGroupFetcher.class);
        when(dataHandlerGroupFetcher.getDataHandlerGroup(any(EzySession.class))).thenReturn(handlerGroup);

        EzySession session = spy(EzyAbstractSession.class);
        when(session.isActivated()).thenReturn(Boolean.TRUE);
        EzyRequestQueue requestQueue = new EzyNonBlockingRequestQueue();
        when(session.getExtensionRequestQueue()).thenReturn(requestQueue);
        EzyArray array = EzyEntityFactory.newArrayBuilder()
            .append(10)
            .build();
        EzySocketRequest request = new EzySimpleSocketRequest(session, array);
        EzySessionTicketsRequestQueues sessionTicketsRequestQueues = new EzySessionTicketsRequestQueues();
        handler.setSessionTicketsQueue(sessionTicketsRequestQueues.getExtensionQueue());
        sessionTicketsRequestQueues.addRequest(request);
        sessionTicketsRequestQueues.addRequest(request);

        handler.setDataHandlerGroupFetcher(dataHandlerGroupFetcher);

        // when
        handler.handleEvent();

        // then
        Asserts.assertEquals(sessionTicketsRequestQueues.getExtensionQueue().size(), 1);
    }

    public static class ExEzySocketRequestHandler extends EzySocketRequestHandler {

        @Override
        protected String getRequestType() {
            return "test";
        }

        @Override
        protected EzyRequestQueue getRequestQueue(EzySession session) {
            return session.getExtensionRequestQueue();
        }

    }
}
