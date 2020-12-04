package galle.yi.radixtrie.me;

import java.util.TreeMap;

/**
 * radix Trie implementation in Java
 * @author galle.yi
 * @link yiyikai@126.com
 * @link 824564329@qq.com
 * @see https://github.com/galleChristian/java-datastructure-radixtree
 * @apiNote only a-z UTF-8 for every public method in this class
 */
public class RedblackRadixTrieMap {

	private Node root;

	private int size;

	private int modCount;

	// -------------
	/**
	 * 
	 * @param key only a-z UTF-8
	 * @return
	 */
	public boolean startWith(String key) {
		if (isEmpty(key)) {
			throw new IllegalArgumentException("key must be not null");
		}
		Node x = prefix(key);
		if (x == null) {
			return false;
		}
		return true;
	}

	private Node prefix(String key) {
		Node p = root;
		if (p == null) {
			return null;
		}
		byte[] chars = key.getBytes();// a-z UTF-8
		int len = chars.length;
		// layer == 0 is starting from children of root
		for (int layer = 0; layer < len;) {
			if (p.children == null) {// check children
				return null;
			}
			byte idx = chars[layer];
			if ((p = childAt(p, idx)) == null) {
				return null;
			}
			// ------------------
			layer = compare(p, layer + 1, chars, len - layer);
			if (layer < 1) {
				return null;
			}
			// ------------------
		}
		return p;
	}

	private static int compare(Node p, int layer, byte[] chars, int remaining) {
		byte[] indices = p.indices;
		int sz = indices.length;
		int min = Math.min(sz, remaining);
		for (int i = 1; i < min; i++, layer++) {// 0 has been compared
			if (indices[i] != chars[layer]) {
				return 0;// not match
			}
		}
		return layer;
	}

	// ----------------------
	/**
	 * 
	 * @param key only a-z UTF-8
	 * @return true only if key is word
	 */
	public boolean contains(String key) {
		if (isEmpty(key)) {
			throw new IllegalArgumentException("key must be not null");
		}
		Node x = doGet(key);
		if (x == null) {
			return false;
		}
		if (x.info == null) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param key only a-z UTF-8
	 * @return not null only if key is word
	 */
	public String get(String key) {
		if (isEmpty(key)) {
			throw new IllegalArgumentException("key must be not null");
		}
		Node x = doGet(key);
		if (x == null) {
			return null;
		}
		return x.info;
	}

	private Node doGet(String key) {
		Node p = root;
		if (p == null) {
			return null;
		}
		byte[] chars = key.getBytes();// a-z UTF-8
		int len = chars.length;
		// layer == 0 is starting from children of root
		for (int layer = 0; layer < len;) {
			if (p.children == null) {// check children
				return null;
			}
			byte idx = chars[layer];
			if ((p = childAt(p, idx)) == null) {
				return null;
			}
			// ------------------
			byte[] indices = p.indices;
			int sz = indices.length;
			// for every comparing, KEY must at least reach to the end of INDICES
			int remaining = len - layer;
			if (remaining < sz) {
				// get "car" from "company"
				return null;
			}
			int cmn = commonPrefixLength(indices, sz, layer + 1, chars, remaining);
			if (cmn < sz) {
				// get "competent" from "company"
				return null;
			}
			// ------------------
			layer += sz;
		}
		return p;
	}

	private static int commonPrefixLength(byte[] indices, int sz, int layer, byte[] chars, int remaining) {
		int count = 1;
		int min = Math.min(sz, remaining);
		for (int i = 1; i < min; i++, layer++) {
			if (indices[i] != chars[layer]) {
				break;// not match
			}
			count++;
		}
		return count;
	}

	// -------

	/**
	 * 
	 * @param key  only a-z UTF-8
	 * @param info
	 * @return
	 */
	public String put(String key, String info) {
		if (isEmpty(key)) {
			throw new IllegalArgumentException("key must be not null");
		}
		if (isEmpty(info)) {
			// please call method remove
			throw new IllegalArgumentException("info must be not null");
		}
		return doPut(key, info);
	}

	/**
	 * To insert a string, we search the tree until we can make no further progress.
	 * <p>
	 * At this point we either add a new outgoing edge labeled with all remaining
	 * elements
	 * <p>
	 * in the input string, or if there is already an outgoing edge sharing a prefix
	 * <p>
	 * with the remaining input string, we split it into two edges (the first
	 * labeled
	 * <p>
	 * with the common prefix) and proceed. This splitting step ensures that
	 * <p>
	 * no node has more children than there are possible string elements.
	 * <p>
	 * Several cases of insertion are shown below, though more may exist.
	 * <p>
	 * Note that r simply represents the root.
	 * <p>
	 * It is assumed that edges can be labelled
	 * <p>
	 * with empty strings to terminate strings
	 * <p>
	 * where necessary and that the root has no incoming edge.
	 * <p>
	 * (The lookup algorithm described above will not work when using empty-string
	 * edges.)
	 * 
	 * @param key
	 * @param info
	 * @return
	 */
	private String doPut(String key, String info) {
		Node p = root;
		if (p == null) {
			root = p = new Node();
		}
		byte[] chars = key.getBytes();
		int len = chars.length;
		TreeMap<Byte, Node> map = null;
		// layer == 0 is starting from children of root
		for (int layer = 0; layer < len;) {
			// ------------------
			map = p.children;
			if (map == null) {
				p.children = map = new TreeMap<Byte, Node>();
			}
			byte idx = chars[layer];
			// ------------------
			int remaining = len - layer;
			Node kid = map.get(Byte.valueOf(idx));
			if (kid == null) {
				modCount++;
				byte[] arr = new byte[remaining];
				System.arraycopy(chars, layer, arr, 0, remaining);
				map.put(Byte.valueOf(idx), kid = new Node(arr, p));
				p = kid;
				break;
			}
			// ------------------
			byte[] indices = kid.indices;
			int sz = indices.length;
			int cmn = commonPrefixLength(indices, sz, layer + 1, chars, remaining);
			if (cmn < sz) {
				modCount++;
				byte[] arrSon = new byte[cmn];
				System.arraycopy(indices, 0, arrSon, 0, cmn);
				Node son = new Node(arrSon, p);
				repalceChildAt(p, arrSon[0], son);
				// -------------
				byte[] arr = new byte[sz - cmn];
				System.arraycopy(indices, cmn, arr, 0, sz - cmn);
				kid.reset(arr, son);
				// -------------
				son.children = map = new TreeMap<Byte, Node>();
				map.put(Byte.valueOf(arr[0]), kid);
				// ------------------
				if (cmn == remaining) {
					// insert "slow" into "slower"
					p = son;
					break;
				} else {// cmn < remaining
					// insert "slay" into "slower"
					arr = new byte[remaining - cmn];
					System.arraycopy(chars, layer + cmn, arr, 0, remaining - cmn);
					Node node = new Node(arr, son);
					// -------------
					map.put(Byte.valueOf(arr[0]), node);
					p = node;
					break;
				}
			} else {// cmn == sz
				p = kid;
				if (cmn == remaining) {
					// insert "slow" into "slow"
					break;
				} else {// cmn < remaining
					// insert "slower" into "slow"
					layer += sz;
				}
			}
		}
		// ------------
		String oldInfo = p.info;
		p.info = info;
		// ------------
		if (oldInfo == null) {
			size++;
		}
		return oldInfo;
	}

	// ---------------
	/**
	 * 
	 * @param key only a-z UTF-8
	 * @return
	 */
	public String remove(String key) {
		if (isEmpty(key)) {
			throw new IllegalArgumentException("key must be not null");
		}
		return delete(key);
	}

	private String delete(String key) {
		Node x = doGet(key);
		if (x == null) {
			return null;
		}
		String info = x.info;
		delete(x);
		return info;
	}

	/**
	 * To delete a string x from a tree, we first locate the leaf representing x.
	 * <p>
	 * Then, assuming x exists, we remove the corresponding leaf node.
	 * <p>
	 * If the parent of our leaf node has only one other child,
	 * <p>
	 * then that child's incoming label is appended to the parent's incoming label
	 * <p>
	 * and the child is removed.
	 * 
	 * @param x
	 * @return
	 */
	private void delete(Node x) {
		if (x.children != null) {
			if (x.info != null) {
				x.info = null;
				size--;
				// here different form Trie
				boolean b = mergeChildAfterDeletion(x);
				if (b) {
					modCount++;
				}
			}
			return;
		}
		size--;// just once
		// deletion can only occur at the leaf layer
		Node p = x.parent;
		int count = countChildren(p);
		if ((p == root) && (count == 1)) {
			// leave nothing
			root = null;
			size = 0;
			modCount = 0;
			free(p);
			free(x);
			return;
		}
		deleteChild(p, x);
		modCount++;
		// ------------
		if (p.info != null) {// pa is word
			if (count == 1) {
				p.children = null;
			}
			return;
		}
		// here p.info == null, p is not word.
		if (count > 2) {
			return;
		}
		// here must be count == 2;
		// if count == 1, x and p both are words.
		boolean b = mergeChildAfterDeletion(p);
		if (b) {
			modCount++;
		}
		return;
	}

	/**
	 * for instance delete "the" with only one child "their"
	 * 
	 * @param x
	 * @apiNote if x.parent is also word, x may has no sibling if x is word; if
	 *          x.parent is not word, x must have at least one sibling;
	 */
	private static boolean mergeChildAfterDeletion(Node x) {
		if (x.children.size() == 1) {
			Node pa = x.parent;
			Node grdSon = x.children.firstEntry().getValue();
			byte[] arr = append(x.indices, grdSon.indices);
			grdSon.reset(arr, pa);// do not touch grdSon.info
			repalceChildAt(pa, x.indices[0], grdSon);
			free(x);
			return true;
		}
		return false;
	}

	// ---------------
	public int size() {
		return size;
	}

	// -----------------------
	private static boolean isEmpty(String s) {
		if (s == null) {
			return true;
		}
		if ("".equals(s.trim())) {
			return true;
		}
		return false;
	}

	private static int countChildren(Node p) {
		return p.children.size();
	}

	private static Node childAt(Node p, byte idx) {
		return p.children.get(Byte.valueOf(idx));
	}

	private static void deleteChild(Node p, Node x) {
		p.children.remove(Byte.valueOf(x.indices[0]));
	}

	private static void repalceChildAt(Node p, byte idx, Node node) {
		p.children.put(Byte.valueOf(idx), node);
	}

	private static void free(Node x) {
		x.indices = null;
		x.parent = null;
		x.children = null;
	}

	// ---------------
	/**
	 * append elements of b at the tail of a, return a new byte[]
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private static byte[] append(byte[] a, byte[] b) {
		int lena = a.length;
		int lenb = b.length;
		byte[] c = new byte[lena + lenb];
		System.arraycopy(a, 0, c, 0, lena);
		System.arraycopy(b, 0, c, lena, lenb);
		return c;
	}

	// ------------
	private static final class Node {

		// here we do not adopt linked list
		// for saving space in respect to primitive type byte
		byte[] indices;// variable length array; if null, is root
		String info;// null is non-word; 
		String content;// just for debug

		Node parent;

		// HashMap 16-32 space waste
		TreeMap<Byte, Node> children;//null or [1, 26]

		/**
		 * just for newly creating root node
		 */
		Node() {
			super();
		}

		Node(byte[] indices, Node parent) {
			super();
			this.indices = indices;
			this.parent = parent;
			this.content = new String(indices);
		}

		void reset(byte[] indices, Node parent) {
			this.indices = indices;
			this.parent = parent;
			this.content = new String(indices);
		}

	}

}
