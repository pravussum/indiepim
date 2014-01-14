package net.mortalsilence.indiepim.server.comet;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class CometService {

    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");

    public CometService() {
        System.err.println("Instiating Comet service");
    }

    public Map<Long, Map<String, BlockingQueue<CometMessage>>> user2SessionsMap = new HashMap<Long, Map<String, BlockingQueue<CometMessage>>>();


    public void sendCometMesssages(final Long userId, final CometMessage message) {
		/* Send comet messages to clients: New messages / last sync run */
        final Map<String, BlockingQueue<CometMessage>> sessionId2CometMsgQueue = user2SessionsMap.get(userId);
        if(sessionId2CometMsgQueue == null)
            return;
        for(Map.Entry<String, BlockingQueue<CometMessage>> entry : sessionId2CometMsgQueue.entrySet()) {
            if(logger.isDebugEnabled()) {
                logger.debug("Firing comet event " + message.getClass().getSimpleName() + " for user id " + userId + ", session " + entry.getKey());
            }
            final BlockingQueue<CometMessage> cometQueue = entry.getValue();
            cometQueue.add(message);
        }
    }

    public BlockingQueue<CometMessage> getCometMessageQueue(final Long userId, final String sessionId) {
        final Map<String, BlockingQueue<CometMessage>> sessionId2CometMsgQueue = user2SessionsMap.get(userId);
        if(sessionId2CometMsgQueue == null)
            throw new RuntimeException("No sessions for user with id " + userId);
        return sessionId2CometMsgQueue.get(sessionId);
    }

    public void addSession(final Long userId, final String sessionId) {
        Map<String, BlockingQueue<CometMessage>> sessionId2CometMsgQueue = user2SessionsMap.get(userId);
        if(sessionId2CometMsgQueue == null) {
            sessionId2CometMsgQueue = new HashMap<String, BlockingQueue<CometMessage>>();
            user2SessionsMap.put(userId, sessionId2CometMsgQueue);
        }
        if(sessionId2CometMsgQueue.containsKey(sessionId))
            return;
        sessionId2CometMsgQueue.put(sessionId, new LinkedBlockingQueue<CometMessage>());
    }

    public void removeSession(final Long userId, final String sessionId) {
        final Map<String, BlockingQueue<CometMessage>> sessionId2CometMsgQueue = user2SessionsMap.get(userId);
        if(sessionId2CometMsgQueue == null)
            throw new RuntimeException("No sessions for user with id " + userId);
        sessionId2CometMsgQueue.remove(sessionId);
    }

    public void transferSessionCometMsgQueue(final Long userId, final String oldSessionId, final String newSessionId) {
        Map<String, BlockingQueue<CometMessage>> sessionId2CometMsgQueue = user2SessionsMap.get(userId);
        if(sessionId2CometMsgQueue == null) {
            sessionId2CometMsgQueue = new HashMap<String, BlockingQueue<CometMessage>>();
            user2SessionsMap.put(userId, sessionId2CometMsgQueue);
        }
        if(sessionId2CometMsgQueue.containsKey(oldSessionId)) {
            sessionId2CometMsgQueue.put(newSessionId, sessionId2CometMsgQueue.get(oldSessionId));
            removeSession(userId, oldSessionId);
        } else {
            addSession(userId, newSessionId);
        }
    }

}
