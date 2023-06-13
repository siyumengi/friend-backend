package com.sym.friend.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author siyumeng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultMessage {
    private boolean isSystem;
    private String fromName;
    private String nowTime;
    private Object message;
}
