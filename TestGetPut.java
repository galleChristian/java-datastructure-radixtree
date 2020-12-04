package galle.yi.radixtrie.me;

/**
 * radix Trie implementation in Java
 * @author galle.yi
 * @link yiyikai@126.com
 * @link 824564329@qq.com
 * @see https://github.com/galleChristian/java-datastructure-radixtree
 * @apiNote only a-z UTF-8 for every public method in this class
 */
public class TestGetPut {

	public static void main(String[] args) {

		String[] arr = { "tester", "slow", "water", "slower", 
				"test", "team", "toast", "today" };

		RedblackRadixTrieMap map = new RedblackRadixTrieMap();
		for (String s : arr) {
			map.put(s, s + " de meanling");
		}
		System.out.println(map.size());

		System.out.println("---startWith---");
		for (String s : arr) {
			String str = s.substring(0, 3);
			boolean b = map.startWith(str);
			if(!b) {
				System.out.println(str + " " + b);
			}
		}
		
		System.out.println("---contains---");
		for (String s : arr) {
			boolean b = map.contains(s);
			if(!b) {
				System.out.println(s + " " + b);
			}
		}
		
		map.put(arr[2], arr[2] + " de explanation");
		map.put(arr[4], arr[4] + " de explanation");
		map.put(arr[6], arr[6] + " de explanation");
		
		System.out.println("---get---");
		for (String s : arr) {
			String info = map.get(s);
			System.out.println(info);
		}
	}
}
