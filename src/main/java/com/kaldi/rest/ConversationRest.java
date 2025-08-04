package com.kaldi.rest;

import com.kaldi.dto.*;
import com.kaldi.model.*;
import com.kaldi.service.ConversationService;
import com.kaldi.service.MessageService;
import com.kaldi.service.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

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
    @RolesAllowed("operator")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all pending conversations that the operator can take on.")
    @APIResponse(responseCode = "200", description = "List of pending conversations",
            content = @Content(schema = @Schema(implementation = ConversationDTO.class)))
    public Response getPendingConversations() {
        List<ConversationDTO> pendingConversations = conversationService.getPendingConversations().stream().map(o -> new ConversationDTO(o.getCustomer().getUsername(), o.getRoom().name(), o.getCreatedAt())).toList();

        return Response.ok(new ConversationsDTO(pendingConversations)).build();
    }

    @GET
    @Path("/taken")
    @RolesAllowed("operator")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all the conversations that the current operator took on.")
    @APIResponse(responseCode = "200", description = "List of taken conversations",
            content = @Content(schema = @Schema(implementation = ConversationDTO.class)))
    public Response getTakenConversations() {
        List<ConversationDTO> takenConversations = conversationService.getTakenConversations().stream().map(o -> new ConversationDTO(o.getCustomer().getUsername(), o.getRoom().name(), o.getCreatedAt())).toList();

        return Response.ok(new ConversationsDTO(takenConversations)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("operator")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get conversation by id.")
    @APIResponse(responseCode = "200", description = "Conversation",
            content = @Content(schema = @Schema(implementation = ConversationDetailsDTO.class)))
    public Response getConversationById(@PathParam("id") Long id) {
        Conversation conversation = conversationService.getConversationById(id);

        return Response.ok(new ConversationDetailsDTO(conversation.getId(), conversation.getCustomer().getUsername(), conversation.getRoom().name(), conversation.getStatus().name(), conversation.getCreatedAt(), conversation.getTakenAt())).build();
    }

    @POST
    @Path("/{id}/take")
    @RolesAllowed("operator")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Take over conversation (operator).")
    @APIResponse(responseCode = "200")
    public Response takeConversation(@PathParam("id") Long id) {
        String username = securityIdentity.getPrincipal().getName();
        Operator operator = (Operator)userService.getUser(username);
        conversationService.takeConversation(id, operator);

        return Response.ok("{}").build();
    }

    @POST
    @Path("/{id}/messages")
    @RolesAllowed({"operator", "customer"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Post a message to conversation with id {id}.")
    @APIResponse(responseCode = "200")
    public Response postMessage(@PathParam("id") Long id, MessageDTO messageDTO) {
        Conversation conversation = conversationService.getConversationById(id);
        messageService.createMessage(conversation, messageDTO.content(), messageDTO.timestamp());

        return Response.ok("{}").build();
    }

    @GET
    @Path("/rooms")
    @RolesAllowed("customer")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all the \"rooms\" that the customer can choose from.")
    @APIResponse(responseCode = "200", description = "List of rooms",
            content = @Content(schema = @Schema(implementation = RoomDTO.class)))
    @Transactional
    public Response getRooms() {
        List<RoomDTO> roomsDTOS = Room.ALL_ROOMS.stream().map(o -> new RoomDTO(o.name())).toList();

        return Response.ok(new RoomsDTO(roomsDTOS)).build();
    }

    @GET
    @RolesAllowed("customer")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all the conversations that the current customer started.")
    @APIResponse(responseCode = "200", description = "List of conversations.",
            content = @Content(schema = @Schema(implementation = ConversationDTO.class)))
    public Response getCustomerConversations() {
        String username = securityIdentity.getPrincipal().getName();
        Long customerId = userService.getUser(username).getId();
        List<ConversationDTO> conversationDTOS = conversationService.getCustomerConversations(customerId).stream().map(o -> new ConversationDTO(o.getCustomer().getUsername(), o.getRoom().name(), o.getCreatedAt())).toList();

        return Response.ok(new ConversationsDTO(conversationDTOS)).build();
    }

    @GET
    @Path("/{id}/messages")
    @RolesAllowed("customer")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all the exchanged messages for the conversation with id {id}.")
    @APIResponse(responseCode = "200", description = "List of messages",
            content = @Content(schema = @Schema(implementation = MessageDTO.class)))
    public Response getConversationMessages(@PathParam("id") Long id) {
        List<MessageDTO> messageDTOS = messageService.getMessagesForConversation(id).stream().map(o -> new MessageDTO(o.getContent(), o.getTimestamp(), o.getSender().getUserType().name())).toList();

        return Response.ok(new MessagesDTO(id, messageDTOS)).build();
    }

    @POST
    @RolesAllowed("customer")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Start a new conversation.")
    @APIResponse(responseCode = "200")
    public Response startConversation(StartConversationDTO startConversationDTO) {
        String username = securityIdentity.getPrincipal().getName();
        Customer customer = (Customer) userService.getUser(username);
        Room room = Room.valueOf(startConversationDTO.roomName());

        conversationService.startConversation(customer, room, new Date(), startConversationDTO.message());
        return Response.ok("{}").build();
    }
}
