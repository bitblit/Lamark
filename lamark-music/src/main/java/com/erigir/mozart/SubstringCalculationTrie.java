package com.erigir.mozart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SubstringCalculationTrie
{
    private node root=new node();
    private int nodeCount = 1;
    
    public SubstringCalculationTrie()
    {
        super();
    }
    
    public static SubstringCalculationTrie build(List objects)
    {
        SubstringCalculationTrie rval = new SubstringCalculationTrie();
        
        /* BEFORE WE ALLOCATE ANY DATA STORAGE, VALIDATE ARGS */
        if(null==objects)
        {
            return null;
        }
        /* Build the suffix trie */
        for(int i=0;i<objects.size();i++)
        {
           rval.insert(objects.subList(i,objects.size())); 
        }
        
        return rval;
    }
    
    public String toString()
    {
        return asString(root,new ArrayList());
    }
    
    public Map<List,Integer> substringMap()
    {
    	Map<List,Integer> rval = new HashMap<List,Integer>();
    	recursiveBuildSubstringMap(rval,new ArrayList(),root);
    	return rval;
    }
    
    private void recursiveBuildSubstringMap(Map<List,Integer> returnValue,List line,node current)
    {
    	int branchSize = current.children.size();
    	if (current.valueNode)
    	{
    		branchSize++;
    	}
    	if (branchSize>=2)
    	{
    		returnValue.put(line,branchSize);
    	}
        for (Iterator keys = current.children.keySet().iterator();keys.hasNext();)
        {
            Object o = keys.next();
            List newList = new ArrayList(line.size()+1);
            newList.addAll(line);
            newList.add(o);
            recursiveBuildSubstringMap(returnValue,newList,current.children.get(o));
        }
    }
    
    private String asString(node val,List previous)
    {
        StringBuffer sb = new StringBuffer();
        if (val.valueNode)
        {
            sb.append("WORD: ");
        }
        else
        {
            sb.append("INTERNAL: ");
        }
        sb.append(previous.toString());
        sb.append(" DEPTH:"+previous.size());
        if (val.children.size()>1)
        {
            sb.append(" REPEATING SUBSTRING");
        }
        sb.append("\n");
        for (Iterator keys = val.children.keySet().iterator();keys.hasNext();)
        {
            Object o = keys.next();
            List newList = new ArrayList(previous.size()+1);
            newList.addAll(previous);
            newList.add(o);
            sb.append(asString(val.children.get(o),newList));
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public void insert(List value)
    {
        if (null!=value) // Ignore nulls
        {
            recursiveInsert(value,root);
        }
    }
    
    public boolean find(List value)
    {
        if (null!=value)
        {
            return recursiveFind(value,root);
        }
        else
        {
            return false;
        }
    }
    
    private boolean recursiveFind(List value, node current)
    {
        if (null==current)
        {
            return false;
        }
        if (value.size()==0)
        {
            return current.valueNode;
        }
        return recursiveFind(value.subList(1,value.size()),current.children.get(value.get(0)));
    }
    
    private void recursiveInsert(List value,node current)
    {
        if (0==value.size())
        {
            current.valueNode=true;
        }
        else
        {
            node child = current.children.get(value.get(0));
            if (null==child)
            {
               child = new node();
               current.children.put(value.get(0),child);
               nodeCount++;
            }
            recursiveInsert(value.subList(1,value.size()),child);
        }            
    }

    static class node
    {
        Map <Object,node> children= new HashMap<Object,node>();
        boolean valueNode = false;
    }

    public int getNodeCount()
    {
        return nodeCount;
    }
}
