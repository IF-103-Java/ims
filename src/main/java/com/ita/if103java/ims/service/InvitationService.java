package com.ita.if103java.ims.service;

public interface InvitationService {
    void inviteUser(String email, Long accountId, String message);
}
