package com.almondtools.rexlex.automaton;

import static java.lang.Character.MAX_VALUE;
import static java.lang.Character.MIN_VALUE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class BitMaskCharClassMapperTest {

	@Test
	public void testGetIndex() throws Exception {
		BitMaskCharClassMapper mapper = new BitMaskCharClassMapper(new char[]{MIN_VALUE, 'c','h','o'});
		assertThat(mapper.getIndex(MIN_VALUE), equalTo(0));
		assertThat(mapper.getIndex('b'), equalTo(0));
		assertThat(mapper.getIndex('c'), equalTo(1));
		assertThat(mapper.getIndex('g'), equalTo(1));
		assertThat(mapper.getIndex('h'), equalTo(2));
		assertThat(mapper.getIndex('n'), equalTo(2));
		assertThat(mapper.getIndex('o'), equalTo(3));
		assertThat(mapper.getIndex(MAX_VALUE), equalTo(3));
	}

	@Test
	public void testGetIndex2() throws Exception {
		BitMaskCharClassMapper mapper = new BitMaskCharClassMapper(new char[]{(char) 0, 'Z','a','b', (char) 0x1111});
		
		assertThat(mapper.getIndex((char) 0),equalTo(0));
		assertThat(mapper.getIndex('a'),equalTo(2));
		assertThat(mapper.getIndex('b'),equalTo(3));
		assertThat(mapper.getIndex('c'),equalTo(3));
		assertThat(mapper.getIndex('Z'),equalTo(1));
		assertThat(mapper.getIndex((char) 0x1000),equalTo(3));
		assertThat(mapper.getIndex((char) 0x1111),equalTo(4));
		assertThat(mapper.getIndex((char) 0x11ff),equalTo(4));
		assertThat(mapper.getIndex((char) 0xffff),equalTo(4));
	}

	@Test
	public void testGetRelevantChars() throws Exception {
		BitMaskCharClassMapper mapper = new BitMaskCharClassMapper(new char[]{MIN_VALUE, 'c','h','o'});
		assertThat(charList(mapper.getRelevantChars()), contains(MIN_VALUE, 'c','h','o'));
	}

	private List<Character> charList(char[] chars) {
		List<Character> list = new ArrayList<Character>();
		for (char c : chars) {
			list.add(c);
		}
		return list;
	}

	@Test
	public void testIndexCount() throws Exception {
		BitMaskCharClassMapper mapper = new BitMaskCharClassMapper(new char[]{MIN_VALUE, 'c','h','o'});
		assertThat(mapper.indexCount(), equalTo(4));
	}

	@Test
	public void testRepresentative() throws Exception {
		BitMaskCharClassMapper mapper = new BitMaskCharClassMapper(new char[]{MIN_VALUE, 'c','h','o'});
		assertThat(mapper.representative(0), equalTo(MIN_VALUE));
		assertThat(mapper.representative(1), equalTo('c'));
		assertThat(mapper.representative(2), equalTo('h'));
		assertThat(mapper.representative(3), equalTo('o'));
	}

	@Test
	public void testComputeHighByte0() throws Exception {
		int[] high = BitMaskCharClassMapper.computeHighByte(new char[]{MIN_VALUE, 'a','b'});
		
		assertThat(high.length, equalTo(256));
		assertThat(high[0], equalTo(0));
		assertThat(high[1], equalTo(1));
		assertThat(high[255], equalTo(1));
	}

	@Test
	public void testComputeLowByte0() throws Exception {
		int[][] low = BitMaskCharClassMapper.computeLowByte(new char[]{MIN_VALUE, 'a','b'});
		
		assertThat(low.length, equalTo(2));
		assertThat(low[0][0], equalTo(0));
		assertThat(low[0][1], equalTo(0));
		assertThat(low[0]['a' & 0xff], equalTo(1));
		assertThat(low[0]['b' & 0xff], equalTo(2));
		assertThat(low[0]['c' & 0xff], equalTo(2));
		assertThat(low[0][255], equalTo(2));
		assertThat(low[1][0], equalTo(2));
		assertThat(low[1][255], equalTo(2));
	}

	@Test
	public void testComputeHighByte1() throws Exception {
		int[] high = BitMaskCharClassMapper.computeHighByte(new char[]{MIN_VALUE, (char) ('a' + 0x0100),(char) ('b' + 0x0100)});
		
		assertThat(high.length, equalTo(256));
		assertThat(high[0], equalTo(0));
		assertThat(high[1], equalTo(1));
		assertThat(high[2], equalTo(2));
		assertThat(high[255], equalTo(2));
	}

	@Test
	public void testComputeLowByte1() throws Exception {
		int[][] low = BitMaskCharClassMapper.computeLowByte(new char[]{MIN_VALUE, (char) ('a' + 0x0100),(char) ('b' + 0x0100)});
		
		assertThat(low.length, equalTo(3));
		assertThat(low[0][0], equalTo(0));
		assertThat(low[0][255], equalTo(0));
		assertThat(low[1][1], equalTo(0));
		assertThat(low[1]['a' & 0xff], equalTo(1));
		assertThat(low[1]['b' & 0xff], equalTo(2));
		assertThat(low[1]['c' & 0xff], equalTo(2));
		assertThat(low[1][255], equalTo(2));
		assertThat(low[2][0], equalTo(2));
		assertThat(low[2][255], equalTo(2));
	}

	@Test
	public void testComputeHighByte12() throws Exception {
		int[] high = BitMaskCharClassMapper.computeHighByte(new char[]{MIN_VALUE, (char) ('a' + 0x0100),(char) ('b' + 0x0200)});
		
		assertThat(high.length, equalTo(256));
		assertThat(high[0], equalTo(0));
		assertThat(high[1], equalTo(1));
		assertThat(high[2], equalTo(2));
		assertThat(high[3], equalTo(3));
		assertThat(high[255], equalTo(3));
	}

	@Test
	public void testComputeLowByte12() throws Exception {
		int[][] low = BitMaskCharClassMapper.computeLowByte(new char[]{MIN_VALUE, (char) ('a' + 0x0100),(char) ('b' + 0x0200)});
		
		assertThat(low.length, equalTo(4));
		assertThat(low[0][0], equalTo(0));
		assertThat(low[0][255], equalTo(0));
		assertThat(low[1][1], equalTo(0));
		assertThat(low[1]['a' & 0xff], equalTo(1));
		assertThat(low[1]['b' & 0xff], equalTo(1));
		assertThat(low[1][255], equalTo(1));
		assertThat(low[2][0], equalTo(1));
		assertThat(low[2]['b' & 0xff], equalTo(2));
		assertThat(low[2]['c' & 0xff], equalTo(2));
		assertThat(low[2][255], equalTo(2));
		assertThat(low[2][255], equalTo(2));
		assertThat(low[3][0], equalTo(2));
		assertThat(low[3][255], equalTo(2));
	}

}
