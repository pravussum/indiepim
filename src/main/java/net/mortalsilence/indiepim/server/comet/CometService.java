package net.mortalsilence.indiepim.server.comet;

import net.mortalsilence.indiepim.server.domain.UserPO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class CometService {

    // TODO recognize users going offline and send + process UserOnlineStateMessage

    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");
    final public static Long BROADCAST = -1L;
    final public static Integer ONLINE_TIMEOUT = 60 * 1000;

    public CometService() {
        System.err.println("Instiating Comet service");
    }

    public Map<Long, Map<String, BlockingQueue<CometMessage>>> user2SessionsMap = new HashMap<Long, Map<String, BlockingQueue<CometMessage>>>();
    public Map<Long, Long> user2LastSeenMap = new HashMap<Long, Long>();


    public void updateLastSeenTimestamp(final UserPO user) {
        final long now = System.currentTimeMillis();
        /* user has gone online: send comet broadcast to other users
         * TODO: whitelist (contact list?) */
        if(!user2LastSeenMap.containsKey(user.getId()) || now > (user2LastSeenMap.get(user.getId()) + CometService.ONLINE_TIMEOUT)) {
            sendCometMessages(CometService.BROADCAST, new UserOnlineStateMessage(user.getId(), true, user.getUserName()), user.getId());
        }
        user2LastSeenMap.put(user.getId(), now);
    }

    public Set<Long> getOnlineUsers(final Long myUserId) {
        final Set<Long> userIds = new HashSet<Long>();
        final Long now = System.currentTimeMillis();
        for(final Map.Entry<Long, Long> entry :  user2LastSeenMap.entrySet()) {
            if(now < entry.getValue() + ONLINE_TIMEOUT && !entry.getKey().equals(myUserId))
                userIds.add(entry.getKey());
        }
        return userIds;
    }

    /**
     * Sends a comet message to the specified users browser sessions
     * A user id of BROADCAST (-1) indicates a broadcast, a comet message is therefore sent to ALL users sessions.
     * If a broadcast is sent, the owned browser sessions are not updated
     * @param userId the user id of the user the message is to be sent to or CometService.BROADCAST for all users
     * @param message
     * @return
     */
    public boolean sendCometMessages(final Long userId, final CometMessage message) {
        return sendCometMessages(userId, message, null);
    }

    /**
     * Sends a comet message to the specified users browser sessions
     * A user id of BROADCAST (-1) indicates a broadcast, a comet message is therefore sent to ALL users sessions.
     * If a broadcast is sent, the owned browser sessions are not updated
     * @param userId the user id of the user the message is to be sent to or CometService.BROADCAST for all users
     * @param message
     * @return
     */
    public boolean sendCometMessages(final Long userId, final CometMessage message, final Long excludeUserId) {
        if(BROADCAST.equals(userId)) {
            for(final Map.Entry<Long, Map<String, BlockingQueue<CometMessage>>> sessionMapEntry : user2SessionsMap.entrySet()) {
                if(logger.isDebugEnabled()) {
                    logger.debug("Firing comet broadcast " + message.getClass().getSimpleName() + " for user id " + sessionMapEntry.getKey());
                }
                if(sessionMapEntry.getKey().equals(excludeUserId))
                    continue;
                sendMessageToAllUserSessions(sessionMapEntry.getKey(), message, sessionMapEntry.getValue());
            }
        } else {
            final Map<String, BlockingQueue<CometMessage>> sessionId2CometMsgQueue = user2SessionsMap.get(userId);
            if(sessionId2CometMsgQueue == null || sessionId2CometMsgQueue.isEmpty())
                return false;
            sendMessageToAllUserSessions(userId, message, sessionId2CometMsgQueue);
        }
        return true;
    }

    private void sendMessageToAllUserSessions(Long userId, CometMessage message, Map<String, BlockingQueue<CometMessage>> sessionId2CometMsgQueue) {
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
        if(sessionId2CometMsgQueue.isEmpty()) {
            user2SessionsMap.remove(userId);
        }
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
