package com.kaldi.rest;

import com.kaldi.dto.*;
import com.kaldi.model.*;
import com.kaldi.service.ConversationService;
import com.kaldi.service.MessageService;
import com.kaldi.service.UserService;
import com.mysql.cj.util.StringUtils;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.util.Date;
import java.util.List;

@Path("/conversations")
public class ConversationRest {
    @Inject
    ConversationService conversationService;
    @Inject
    MessageService messageService;
    @Inject
    UserService userService;
    @Inject
    SecurityIdentity securityIdentity;


    @GET
    @Path("/pending")
    @RolesAllowed("OPERATOR")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all pending conversations that the operator can take on.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Pending conversations retrieved"),
            @APIResponse(responseCode = "401", description = "Authentication required"),
            @APIResponse(responseCode = "403", description = "Access denied (available only to operators)")
    })
    public Response getPendingConversations() {
        User user = userService.getUser(securityIdentity.getPrincipal().getName());
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User doesn't exist").build();
        }

        if (user.getUserType() != UserType.OPERATOR) {
            return Response.status(Response.Status.FORBIDDEN).entity("User is not operator").build();
        }

        List<ConversationDTO> pendingConversations = conversationService.getPendingConversations().stream().map(o -> new ConversationDTO(o.getCustomer().getUsername(), o.getRoom().name(), o.getCreatedAt())).toList();

        return Response.ok(new ConversationsDTO(pendingConversations)).build();
    }

    @GET
    @Path("/taken")
    @RolesAllowed("OPERATOR")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all the conversations that the current operator took on.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Taken conversations retrieved"),
            @APIResponse(responseCode = "401", description = "Authentication required"),
            @APIResponse(responseCode = "403", description = "Access denied (available only to operators)")
    })
    public Response getTakenConversations() {
        User user = userService.getUser(securityIdentity.getPrincipal().getName());
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User doesn't exist").build();
        }

        if (user.getUserType() != UserType.OPERATOR) {
            return Response.status(Response.Status.FORBIDDEN).entity("User is not operator").build();
        }

        List<ConversationDTO> takenConversations = conversationService.getTakenConversations().stream().map(o -> new ConversationDTO(o.getCustomer().getUsername(), o.getRoom().name(), o.getCreatedAt())).toList();

        return Response.ok(new ConversationsDTO(takenConversations)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("OPERATOR")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get conversation by id.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Conversation retrieved"),
            @APIResponse(responseCode = "401", description = "Authentication required"),
            @APIResponse(responseCode = "403", description = "Access denied (available only to operators"),
            @APIResponse(responseCode = "404", description = "Conversation not found")
    })
    public Response getConversationById(@PathParam("id") Long id) {
        User user = userService.getUser(securityIdentity.getPrincipal().getName());
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User doesn't exist").build();
        }

        if (user.getUserType() != UserType.OPERATOR) {
            return Response.status(Response.Status.FORBIDDEN).entity("User is not operator").build();
        }

        Conversation conversation = conversationService.getConversationById(id);
        if (conversation == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Conversation doesn't exist").build();
        }

        return Response.ok(new ConversationDetailsDTO(conversation.getId(), conversation.getCustomer().getUsername(), conversation.getRoom().name(), conversation.getStatus().name(), conversation.getCreatedAt(), conversation.getTakenAt())).build();
    }

    @POST
    @Path("/{id}/take")
    @RolesAllowed("OPERATOR")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Take over a pending conversation.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Conversation successfully taken"),
            @APIResponse(responseCode = "401", description = "Authentication required"),
            @APIResponse(responseCode = "403", description = "Access denied (available only to operators)"),
            @APIResponse(responseCode = "404", description = "Conversation not found"),
            @APIResponse(responseCode = "409", description = "Conversation already taken")
    })
    public Response takeConversation(@PathParam("id") Long id) {
        User user = userService.getUser(securityIdentity.getPrincipal().getName());
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User doesn't exist").build();
        }

        if (user.getUserType() != UserType.OPERATOR) {
            return Response.status(Response.Status.FORBIDDEN).entity("User is not operator").build();
        }

        Operator operator = (Operator) user;
        Conversation conversation = conversationService.getConversationById(id);
        if (conversation == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Conversation doesn't exist").build();
        }

        if (conversation.getOperator() != null) {
            return Response.status(Response.Status.CONFLICT).entity("Conversation is already taken by an operator").build();
        }

        conversationService.takeConversation(id, operator);

        return Response.ok("Conversation successfully taken by operator").build();
    }

    @POST
    @Path("/{id}/messages")
    @RolesAllowed({"OPERATOR", "CUSTOMER"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Post a message to conversation with id {id}.")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Successfully added message"),
            @APIResponse(responseCode = "400", description = "Invalid message format"),
            @APIResponse(responseCode = "401", description = "Authentication required"),
            @APIResponse(responseCode = "404", description = "Conversation not found")
    })
    public Response postMessage(@PathParam("id") Long id, MessageDTO messageDTO) {
        String username = securityIdentity.getPrincipal().getName();
        User user = userService.getUser(username);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User doesn't exist").build();
        }

        if (StringUtils.isNullOrEmpty(messageDTO.content())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Message content can't be empty").build();
        }

        Conversation conversation = conversationService.getConversationById(id);
        if (conversation == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Conversation doesn't exist").build();
        }

        messageService.createMessage(conversation, messageDTO.content(), new Date());

        return Response.status(Response.Status.CREATED).entity("Message was added to conversation").build();
    }

    @GET
    @Path("/rooms")
    @RolesAllowed("CUSTOMER")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all the \"rooms\" that the customer can choose from.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Successfully retrieved list of available rooms"),
            @APIResponse(responseCode = "401", description = "Authentication required")
    })
    @Transactional
    public Response getRooms() {
        String username = securityIdentity.getPrincipal().getName();
        User user = userService.getUser(username);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User doesn't exist").build();
        }

        List<RoomDTO> roomsDTOS = Room.ALL_ROOMS.stream().map(o -> new RoomDTO(o.name())).toList();

        return Response.ok(new RoomsDTO(roomsDTOS)).build();
    }

    @GET
    @RolesAllowed("CUSTOMER")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all the conversations that the current customer started.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Conversations retrieved"),
            @APIResponse(responseCode = "401", description = "Authentication required")
    })
    public Response getCustomerConversations() {
        String username = securityIdentity.getPrincipal().getName();
        User user = userService.getUser(username);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User doesn't exist").build();
        }

        Long customerId = user.getId();
        List<ConversationDTO> conversationDTOS = conversationService.getCustomerConversations(customerId).stream().map(o -> new ConversationDTO(o.getCustomer().getUsername(), o.getRoom().name(), o.getCreatedAt())).toList();

        return Response.ok(new ConversationsDTO(conversationDTOS)).build();
    }

    @GET
    @Path("/{id}/messages")
    @RolesAllowed("CUSTOMER")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all the exchanged messages for the conversation with id {id}.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Messages retrieved"),
            @APIResponse(responseCode = "401", description = "Authentication required"),
            @APIResponse(responseCode = "404", description = "Conversation not found")
    })
    public Response getConversationMessages(@PathParam("id") Long id) {
        String username = securityIdentity.getPrincipal().getName();
        User user = userService.getUser(username);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User doesn't exist").build();
        }

        Conversation conversation = conversationService.getConversationById(id);
        if (conversation == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Conversation doesn't exist").build();
        }

        List<MessageDetailsDTO> messageDetailsDTOS = messageService.getMessagesForConversation(id).stream().map(o -> new MessageDetailsDTO(o.getContent(), o.getTimestamp(), o.getSender().getUsername())).toList();

        return Response.ok(new MessagesDTO(id, messageDetailsDTOS)).build();
    }

    @POST
    @RolesAllowed("CUSTOMER")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Start a new conversation.")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Conversation successfully created"),
            @APIResponse(responseCode = "400", description = "Invalid input data"),
            @APIResponse(responseCode = "401", description = "Authentication required")
    })
    public Response startConversation(StartConversationDTO startConversationDTO) {
        String username = securityIdentity.getPrincipal().getName();
        User user = userService.getUser(username);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User doesn't exist").build();
        }

        if (StringUtils.isNullOrEmpty(startConversationDTO.roomName()) || StringUtils.isNullOrEmpty(startConversationDTO.message())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Room and message can't be empty").build();
        }

        Customer customer = (Customer) user;
        Room room = Room.valueOf(startConversationDTO.roomName());
        conversationService.startConversation(customer, room, new Date(), startConversationDTO.message());

        return Response.status(Response.Status.CREATED).entity("Conversation was created").build();
    }
}
