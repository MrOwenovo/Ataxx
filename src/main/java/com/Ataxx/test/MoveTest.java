package com.Ataxx.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MoveTest {


    @Test
    public void testIsCloneTrue(){
        assertEquals(Boolean.valueOf("true"), Move.isClone("c1","d2"));
        assertEquals(Boolean.valueOf("true"), Move.isClone("b1","b2"));
    }

    @Test
    public void testIsCloneFalse(){
        assertEquals(Boolean.valueOf("false"), Move.isClone("c1","c1"));
        assertEquals(Boolean.valueOf("false"), Move.isClone("c1","c3"));
    }


    @Test
    public void testIsJumpTrue(){
        assertEquals(Boolean.valueOf("true"), Move.isJump("c1","d3"));
        assertEquals(Boolean.valueOf("true"), Move.isJump("c1","c3"));
    }

    @Test
    public void testIsJumpFalse(){
        assertEquals(Boolean.valueOf("false"), Move.isJump("c4","d3"));
        assertEquals(Boolean.valueOf("false"), Move.isJump("b2","b5"));
    }


}
