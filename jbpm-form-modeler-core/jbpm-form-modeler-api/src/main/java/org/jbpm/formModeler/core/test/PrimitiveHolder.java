/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.core.test;

import java.io.Serializable;

/**
 * Sample class for demo purposes.
 */
public class PrimitiveHolder implements Serializable {
    private byte pByte;
    private short pShort;
    private int pInt;
    private long pLong;
    private float pFloat;
    private double pDouble;
    private char pChar;
    private boolean pBoolean;

    public boolean getpBoolean() {
        return pBoolean;
    }

    public void setpBoolean( boolean pBoolean ) {
        this.pBoolean = pBoolean;
    }

    public byte getpByte() {
        return pByte;
    }

    public void setpByte( byte pByte ) {
        this.pByte = pByte;
    }

    public char getpChar() {
        return pChar;
    }

    public void setpChar( char pChar ) {
        this.pChar = pChar;
    }

    public double getpDouble() {
        return pDouble;
    }

    public void setpDouble( double pDouble ) {
        this.pDouble = pDouble;
    }

    public float getpFloat() {
        return pFloat;
    }

    public void setpFloat( float pFloat ) {
        this.pFloat = pFloat;
    }

    public int getpInt() {
        return pInt;
    }

    public void setpInt( int pInt ) {
        this.pInt = pInt;
    }

    public long getpLong() {
        return pLong;
    }

    public void setpLong( long pLong ) {
        this.pLong = pLong;
    }

    public short getpShort() {
        return pShort;
    }

    public void setpShort( short pShort ) {
        this.pShort = pShort;
    }
}
