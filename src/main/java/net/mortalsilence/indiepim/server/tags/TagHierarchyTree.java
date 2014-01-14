package net.mortalsilence.indiepim.server.tags;

import net.mortalsilence.indiepim.server.SharedConstants;
import net.mortalsilence.indiepim.server.ds.Node;
import net.mortalsilence.indiepim.server.ds.Tree;

public class TagHierarchyTree extends Tree<TagHierarchyNode> {

	public void addPath(String path, Long leafId) {
		if(rootElement == null) {
            setRootElement(new TagHierarchyNode());
        }
		this.addPath(rootElement, path, leafId);
	}	
	
	private void addPath(TagHierarchyNode localRoot, String path, Long leafId) {
			
		int index = path.indexOf(SharedConstants.TAG_LINEAGE_SEPARATOR);
		
		TagHierarchyNode node;
		if(index == -1) {
			// Leaf
			node = getOrCreateChild(localRoot, path);
			node.data.id = leafId;			
		} else {
			node = getOrCreateChild(localRoot, path.substring(0, index));
			addPath(node, path.substring(index + 1), leafId);
		}
		
	}
	
	protected TagHierarchyNode getOrCreateChild(TagHierarchyNode root, String tag) {
		for (Node<TagHierarchyNodeData> child : root.getChildren()) {
			if(tag.equals(child.data.tag))
				return (TagHierarchyNode)child;
		}
		TagHierarchyNodeData nodeData = new TagHierarchyNodeData();
		nodeData.tag = tag;
		TagHierarchyNode newNode = new TagHierarchyNode(nodeData);
		root.addChild(newNode);
		return newNode;
	}
}
