/**
 *
 */
package eu.watchme.sm.helpers;

import java.util.ArrayList;
import java.util.List;

public final class GeneralNLP {
    public static List<String> combineWords(String[] words) {
		List<String> resWords = new ArrayList<>();
		int sizeList = words.length;
		String[][] allWords = new String[2][words.length];
		allWords[0] = words;
		for(int i=0; i<sizeList; i++)
			allWords[1][i]=words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
		int count = 1;
		int powerSize = (int)Math.pow(2, sizeList);
		int[][] combinations = new int[powerSize][sizeList];
		for(int i=0; i<powerSize; i++)
			for(int j=0; j<sizeList; j++)
				combinations[i][j]=0;
		boolean breakLoop = false;
		for(int i=0; i<powerSize; i++) {
			int j = sizeList;
			while(GeneralNLP.sumRow(combinations[i])<count && !breakLoop) {
				int diff = count - GeneralNLP.sumRow(combinations[i]);
				if(diff>0)
					if(Math.pow(2, j-1)<diff)
						combinations[i][j-1] += Math.pow(2, j-1);
					else
						if(j>1)
							j-=1;
						else
							breakLoop=true;
			}
			breakLoop = false;
			count = count + 1;
		}

		for(int i=0; i<powerSize; i++)
			for(int k=0; k<sizeList; k++)
				combinations[i][k]=(int)(combinations[i][k]/(Math.pow(2, k)));

		for(int i=0; i<powerSize; i++) {
			String tempStr = "";
			for(int k=0; k<sizeList; k++)
				tempStr = tempStr.concat(allWords[combinations[i][k]][k]+" ");
			resWords.add(tempStr.trim());
		}
		return resWords;
    }

    public static int sumRow(int[] row) {
		int total = 0;
		for(int i=0; i<row.length; i++)
			total+=row[i];
		return total;
    }
}
