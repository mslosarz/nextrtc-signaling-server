package org.nextrtc.signalingserver.domain;

public interface Signals {
    String EMPTY = "";
    String EMPTY_HANDLER = "nextRTC_EMPTY";
    String OFFER_REQUEST = "offerRequest";
    String OFFER_REQUEST_HANDLER = "nextRTC_OFFER_REQUEST";
    String OFFER_RESPONSE = "offerResponse";
    String OFFER_RESPONSE_HANDLER = "nextRTC_OFFER_RESPONSE";
    String ANSWER_REQUEST = "answerRequest";
    String ANSWER_REQUEST_HANDLER = "nextRTC_ANSWER_REQUEST";
    String ANSWER_RESPONSE = "answerResponse";
    String ANSWER_RESPONSE_HANDLER = "nextRTC_ANSWER_RESPONSE";
    String FINALIZE = "finalize";
    String FINALIZE_HANDLER = "nextRTC_FINALIZE";
    String CANDIDATE = "candidate";
    String CANDIDATE_HANDLER = "nextRTC_CANDIDATE";
    String PING = "ping";
    String PING_HANDLER = "nextRTC_PING";
    String LEFT = "left";
    String LEFT_HANDLER = "nextRTC_LEFT";
    String JOIN = "join";
    String JOIN_HANDLER = "nextRTC_JOIN";
    String CREATE = "create";
    String CREATE_HANDLER = "nextRTC_CREATE";
    String JOINED = "joined";
    String JOINED_HANDLER = "nextRTC_JOINED";
    String CREATED = "created";
    String CREATED_HANDLER = "nextRTC_CREATED";
    String TEXT = "text";
    String TEXT_HANDLER = "nextRTC_TEXT";
}
