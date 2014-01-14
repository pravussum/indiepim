package net.mortalsilence.indiepim.server.tags;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.mortalsilence.indiepim.server.ds.Node;

import java.util.LinkedList;
import java.util.List;

public class TagHierarchyNode extends Node<TagHierarchyNodeData> {


    public TagHierarchyNode(TagHierarchyNodeData data) {
        super(data);
    }

    public TagHierarchyNode() {
        super(new TagHierarchyNodeData());
    }

    @Override
    @JsonIgnore
    public TagHierarchyNodeData getData() {
        return super.getData();
    }

    @Override
    @JsonIgnore
    public void setData(TagHierarchyNodeData data) {
        super.setData(data);
    }

    @Override
    @JsonIgnore
    public List<Node<TagHierarchyNodeData>> getChildren() {
        return super.getChildren();
    }

    @Override
    @JsonIgnore
    public void setChildren(List<Node<TagHierarchyNodeData>> children) {
        super.setChildren(children);
    }

    @JsonProperty("title")
    public String getName() {
        if(data == null)
            return null;
        return data.tag;
    }

    @JsonProperty("nodes")
    public List<TagHierarchyNode> getChildNodes() {
        if(super.getChildren() == null || super.getChildren().isEmpty())
            return null;
        final List<TagHierarchyNode> result = new LinkedList<TagHierarchyNode>();
        for(Node<TagHierarchyNodeData> node : super.getChildren()) {
            result.add((TagHierarchyNode)node);
        }
        return result;
    }

    public void setChildNodes(List<TagHierarchyNode> childNodes) {
        if(childNodes == null || childNodes.isEmpty())
            return;
        for(TagHierarchyNode curNode : childNodes) {
            super.addChild(curNode);
        }
    }

    public void setName(final String name) {
        data.tag = name;
    }

    @JsonProperty("id")
    public Long getId() {
        if(data == null)
            return null;
        return data.id;
    }

    public void setId(final Long id) {
        data.id = id;
    }
}
