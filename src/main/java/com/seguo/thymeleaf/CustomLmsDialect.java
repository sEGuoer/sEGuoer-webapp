package com.seguo.thymeleaf;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import java.util.HashSet;
import java.util.Set;

public class CustomLmsDialect extends AbstractProcessorDialect {
    /**
     * 定义方言名称
     */
    private static final String NAME = "CustomLmsDialect";

    /**
     * 定义方言属性
     */
    private static final String PREFIX = "lms";

    public CustomLmsDialect() {
        super(NAME, PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
    }

    @Override
    public Set<IProcessor> getProcessors(final String dialectPrefix) {
        final Set<IProcessor> processor = new HashSet<>();
        processor.add(new CustomViteElement(PREFIX));
        return processor;
    }
}
