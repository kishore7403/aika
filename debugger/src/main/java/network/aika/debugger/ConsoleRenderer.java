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
 */
package network.aika.debugger;

import javax.swing.text.*;
import java.awt.*;

/**
 * @author Lukas Molzberger
 */
public interface ConsoleRenderer<E> {

    void render(StyledDocument sDoc, E e);

    default void appendEntry(StyledDocument sDoc, String fieldName, String fieldValue, Color c) {
        appendText(sDoc, fieldName, "bold", c);
        appendText(sDoc, fieldValue + "\n", "regular", c);
    }

    default void appendText(StyledDocument sDoc, String txt, String style, Color c) {
        try {
            final StyleContext cont = StyleContext.getDefaultStyleContext();
            final AttributeSet attr = cont.addAttribute(sDoc.getStyle(style), StyleConstants.Foreground, c);

            sDoc.insertString(sDoc.getLength(), txt, attr);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
