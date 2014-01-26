package net.mortalsilence.indiepim.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.mortalsilence.indiepim.server.calendar.EventDTO;
import net.mortalsilence.indiepim.server.calendar.ICSParser;
import net.mortalsilence.indiepim.server.command.actions.*;
import net.mortalsilence.indiepim.server.command.exception.CommandException;
import net.mortalsilence.indiepim.server.command.handler.*;
import net.mortalsilence.indiepim.server.command.results.*;
import net.mortalsilence.indiepim.server.dao.UserDAO;
import net.mortalsilence.indiepim.server.domain.CalendarPO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import net.mortalsilence.indiepim.server.dto.EmailAddressDTO;
import net.mortalsilence.indiepim.server.dto.MessageAccountDTO;
import net.mortalsilence.indiepim.server.dto.TagDTO;
import net.mortalsilence.indiepim.server.dto.UserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 19.08.13
 * Time: 22:04
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/command")
public class CommandController {

    @Inject private GetMessageAccountsHandler getMessageAccountsHandler;
    @Inject private GetAllMessagesHandler getMessagesHandler;
    @Inject private GetMessageHandler getMessageHandler;
    @Inject private CreateOrUpdateMessageAccountHandler createMsgAccountHandler;
    @Inject private GetCometMessagesHandler getCometMessageHandler;
    @Inject private CreateOrUpdateUserHandler createUserHandler;
    @Inject private GetUsersHandler getUsersHandler;
    @Inject private UserDAO userDAO;
    @Inject private ICSParser icsParser;
    @Inject private MarkMessageAsReadHandler markReadHandler;
    @Inject private GetMessageStatsHandler messageStatsHandler;
    @Inject private GetEmailAddressesHandler getEmailAddressHandler;
    @Inject private SendMessageHandler sendMessageHandler;
    @Inject private GetTagsHandler tagsHandler;
    @Inject private StartAccountSynchronisationHandler accountSyncHandler;
    @Inject private SendChatMessageHandler chatMessageHandler;
    @Inject private DeleteMessagesHandler deleteMessagesHandler;

    @RequestMapping(value="getMessageAccounts", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public GetMessageAccountsResult getMessageAccounts() {
        return getMessageAccountsHandler.execute(new GetMessageAccounts());
    }

    @RequestMapping(value="getMessages", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object getMessages(@RequestParam(value = "accountId", required = false) Long accountId,
                              @RequestParam(value = "tagName", required = false) String tagName,
                              @RequestParam(value = "tagLineageId", required = false) Long tagLineageId,
                              @RequestParam(value = "searchTerm", required = false) String searchTerm,
                              @RequestParam(value = "read", required = false) Boolean read,
                              @RequestParam(value = "offset", required = false) Integer offset,
                              @RequestParam(value = "pageSize", required = false) Integer pageSize
                              ) {
        if(offset == null || offset < 0)
            offset = 0;
        if(pageSize == null || pageSize < 0)
            pageSize = 50;
        final MessageListResult result = getMessagesHandler.execute(new GetMessages(offset, pageSize, accountId, tagName, tagLineageId, searchTerm, read));
        return result;
    }

    @RequestMapping(value="getMessage/{messageId}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object getMessage(@PathVariable(value = "messageId") final Long messageId) {
        try {
            final MessageDTOResult result = getMessageHandler.execute(new GetMessage(messageId));
            return result.getMessageDTO();
        } catch (CommandException e) {
            return new ErrorResult(e.getMessage());
        }
    }

    @RequestMapping(value="markAsRead/{messageId}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object markMessageRead(@PathVariable(value = "messageId") final Long messageId,
                                  @RequestParam(value = "read", required = false) final Boolean readFlag) {
        final List<Long> ids = new LinkedList<Long>();
        final Boolean read = readFlag != null ? readFlag : Boolean.TRUE;
        ids.add(messageId);
        final MessageDTOListResult result = markReadHandler.execute(new MarkMessagesAsRead(ids, read));
        return result.getMessages();
    }

    @RequestMapping(value="sendChatMessage/{userId}", consumes = "text/plain", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object sendChatMessage(@PathVariable(value = "userId") final Long userId,
                                  @RequestBody final String message) {
        return chatMessageHandler.execute(new SendChatMessage(userId, message));
    }

    @RequestMapping(value="createOrUpdateUser", consumes = "application/json", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object createUser(@RequestBody final UserDTO user) {
       return createUserHandler.execute(new CreateOrUpdateUser(user));
    }

    @RequestMapping(value="getUsers", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Collection<UserDTO> getUsers(@RequestParam(value="onlineOnly", required = false) final Boolean onlineOnly) {
        final GetUsers action = onlineOnly != null ? new GetUsers(onlineOnly) : new GetUsers();
        return getUsersHandler.execute(action).getUsers();
    }

     // TODO rework response object (use JSON annotation, specify produce and charset
    @RequestMapping(value="createOrUpdateMessageAccount",  consumes = "application/json", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object createOrUpdateMessageAccount(@RequestBody final String request){

        // try to parse the request json
        final ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            final MessageAccountDTO accountDTO = jsonMapper.readValue(request, MessageAccountDTO.class);
            // TODO validate input
            final IdVersionResult result = createMsgAccountHandler.execute(new CreateOrUpdateMessageAccount(accountDTO));
            return result;
        } catch (IOException e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    @RequestMapping(value="syncMessageAccount/{accountId}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BooleanResult syncMessageAccount(@PathVariable(value = "accountId") final Long accountId,
                                     @RequestParam(value ="full", required = false) final Boolean fullSync) {
        return accountSyncHandler.execute(new StartAccountSynchronisation(accountId, fullSync));
    }

    @RequestMapping(value="getCometMessages", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getCometMessages(HttpSession session) {
        final CometMessagesResult result = getCometMessageHandler.execute(new GetCometMessages(session.getId()));
        final ObjectMapper jsonMapper = new ObjectMapper();
        try {
            return jsonMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    @RequestMapping(value="importics", consumes = "multipart/form-data", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String importIcs(@RequestParam("file") CommonsMultipartFile upload) {
        final UserPO user = userDAO.getUser(ActionUtils.getUserId());
        final CalendarPO newCalendar = icsParser.persistCalendarFromICSFile(user, upload);

        int imported = newCalendar.getEvents().size();
        return "<h3>Import successful</h3><p>Imported " + imported + " events.";
    }


    @RequestMapping(value="getEvents", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object getEvents(@RequestParam("start") final Long start,
                            @RequestParam("end") final Long end) {
        final Collection<EventDTO> events = new LinkedList<EventDTO>();

        return events;
    }

    @RequestMapping(value="getMessageStats", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object getMessageStats(@RequestParam(value = "type") final String statsTypeStr) {
        try {
            final GetMessageStats.STATS_TYPE statsType = GetMessageStats.STATS_TYPE.valueOf(statsTypeStr);
            // TODO generalize
            return messageStatsHandler.execute(new GetMessageStats(statsType)).getLastTenDaysCount();
        } catch(IllegalArgumentException iae) {
            return null;
        }
    }

    @RequestMapping(value="getEmailAddresses", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<EmailAddressDTO> getEmailAddresses (@RequestParam(value="query", required=false) final String query) {
        return getEmailAddressHandler.execute(new GetEmailAddresses(query)).getAddresses();
    }

    /* Encoding for the message to send is explicitly not specified here (may differ from UTF-8)
       -> hopefully handled by Spring MVC and converted to UTF-8 */
    @RequestMapping(value="sendMessage", consumes = "application/json", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object sendMessage (@RequestBody final SendMessage sendMessageAction) {
        return sendMessageHandler.execute(sendMessageAction);
    }

    @RequestMapping(value="getTags", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Collection<TagDTO> searchForTags(@RequestParam(value="tagLineageId", required = false) final Long tagLinageId,
                                            @RequestParam(value="query", required = false) final String query) {
        return tagsHandler.execute(new GetTags(tagLinageId, query)).getTags();
    }

    @RequestMapping(value="deleteMessage/{id}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object deleteMessage(@PathVariable(value = "id") final Long msgId) {
        final List<Long> msgIds = new LinkedList<Long>();
        msgIds.add(msgId);
        return deleteMessagesHandler.execute(new DeleteMessages(msgIds));
    }
}
