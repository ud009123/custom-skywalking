/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package org.apache.skywalking.apm.plugin.jdbc.mysql.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.not;


/**
 * {@link CustomYongda} define that the mysql-2.x plugin intercepts the following methods in the
 * com.mysql.jdbc.CallableStatement
 * 1. execute 
 * 2. executeQuery 
 * 3. executeUpdate 
 *
 * @author zhangxin
 */
public class CustomYongda extends ClassInstanceMethodsEnhancePluginDefine {
   private static final ILog logger = LogManager.getLogger(CustomYongda.class);

    private static final String ENHANCE_CLASS = "com.mysql.jdbc.CallableStatement";
    private static final String SERVICE_METHOD_INTERCEPTOR = "org.apache.skywalking.apm.plugin.jdbc.mysql.define.DefaultCustomMethodInterceptor";

    @Override protected ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    @Override protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new InstanceMethodsInterceptPoint() {
                @Override public ElementMatcher<MethodDescription> getMethodsMatcher() {
                   return isPublic().and(
                         not(isDeclaredBy(Object.class))
                         .and(not(named("getSkyWalkingDynamicField")))
                         .and(not(named("setSkyWalkingDynamicField")))
                         .and(not(named("init")))
                         .and(not(nameContains("intercept")))
                         .and(not(nameContains("invoke")))
								 .and(not(isAnnotatedWith(named("javax.annotation.PostConstruct")))));

                }

                @Override public String getMethodsInterceptor() {
                    return SERVICE_METHOD_INTERCEPTOR;
                }

                @Override public boolean isOverrideArgs() {
                    return false;
                }
            }
        };
    }

    @Override protected ClassMatch enhanceClass() {
        return MultiClassNameContainsMatch.byMultiClassMatch("yongda.core","yongda.facades","yongda.integration");
    }
}
