package com.partyst.app.partystapp.records.responses;

import java.util.List;

public record EditUserResponse(String name, String lastname, String nickname, String cellphone, List<SkillResponse> skills, String email) {

}
