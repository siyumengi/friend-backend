package com.sym.friend.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 22603
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private boolean flag;
    private String message;
}
