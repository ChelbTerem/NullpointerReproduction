package com.reproduction.nullpointer;

import com.amazon.ask.Skill;
import com.amazon.ask.Skills;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.ResponseEnvelope;

public class MySkill {
    public static final String SKILL_ID = "amzn1.ask.skill.4984389b-f53c-475b-8da3-1b2462fa8e2e";
    private final Skill skill;


    public MySkill() {
        this(SkillBuilder.getSkill());
    }

    public MySkill(Skill xSkill) {
        this.skill = xSkill;
    }

    public ResponseEnvelope handle(RequestEnvelope xRequestEnvelope) {
        if (xRequestEnvelope == null)
            return null;

        return this.skill.invoke(xRequestEnvelope);
    }


    private static class SkillBuilder {

        public static Skill getSkill() {

            return Skills.standard()
                    .withSkillId(SKILL_ID)
                    .addRequestHandlers(new LaunchHandler())
                    .build();
        }
    }
}
