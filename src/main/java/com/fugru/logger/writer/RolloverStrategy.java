package com.fugru.logger.writer;

import java.io.File;

public interface RolloverStrategy {
    File getCurrentFile();
    boolean rollover();
}
