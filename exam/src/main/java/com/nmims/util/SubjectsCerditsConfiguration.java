package com.nmims.util;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author siddheshwar_khanse
 *
 */
public interface SubjectsCerditsConfiguration {
	public static final List<Integer> mbawx4SubjectCreditsPSSIdsList = Arrays.asList(2558,2559,2560,2561,2562,2563,2564,2565,2566,2567);
	public static final List<Integer> mbawx8SubjectCreditsPSSIdsList = Arrays.asList(2619);
	public static final List<Integer> mbawx3_5SubjectCreditsPSSIdsList = Arrays.asList(2568,2569,2570,2571,2572,2573,2574,2575,2576,2577,2578,2579,2580,2581,2582,2583,2584,2585,
			2586,2587,2588,2589,2590,2591,2592,2593,2594,2595,2596,2597,2598,2599,2600,2601,2602,2603,2604,2605,2606,2607,2608,2609,2610,2611,2612,2613,2614,2615,2616,2617,
			2618,2622,2623,2624,2625,2626,2627,2628,2629,2630,2631,2632,2633,2634,2635,2636,2637,2638,2639,2640,2641,2642,2643,2644,2645,2646,2647,2648,2649,2650,2651,2652,
			2653,2654,2655,2656,2657,2658,2659,2660,2661,2662,2663,2664,2665,2666,2667,2668,2669,2670,2671,2672,2673,2674,2675);

	public static double getMSCAIOpsSubjectCredits(String subject) {
		switch (subject) {
		case "Foundations of Probability and Statistics for Data Science":
			return 5;
		case "Data Structures and Algorithms":
			return 4;
		case "Advanced Mathematical Analysis for Data Science":
			return 3;
		case "Essential Engineering Skills in Big Data Analytics Using R and Python":
			return 3;
		case "Statistics and Probability in Decision Modeling- 1":
			return 3;
		case "Statistics and Probability in Decision Modeling -2":
			return 3;
		case "Advanced Data Structures and Algorithms":
			return 2;
		case "The Art and Science of Storytelling and Visualization & Design Thinking-1":
			return 3;
		case "Methods and Algorithms in Machine Learning -1":
			return 4;
		case "Methods and Algorithms in Machine Learning -2":
			return 4;
		case "Methods and Algorithms in Machine Learning -3":
			return 3;
		case "AI and Decision Sciences":
			return 4;
		case "Computer vision fundamentals and deep learning applications":
			return 5;
		case "Text mining and Natural Language Processing Deep learning for NLP":
			return 5;
		case "Big Data: An overview of Big Data and Hadoop ecosystems":
			return 5;
		case "Quantum Computing":
			return 5;		
		case "Advanced Python Programming":
			return 3;
		case "Business Communication and Presentation Skills for Data Analytics":
			return 1;
		case "Economics for Analysts":
			return 2.5;
		case "Business Law and Ethics":
			return 2.5;
		case "Behavioural Science and Analytics":
			return 2.5;
		case "Digital and Social Media Analytics":
			return 2.5;
		case "Product Management":
			return 2.5;
		case "Project Management":
			return 2.5;
		case "Architecting Enterprise Applications and Design Thinking-2":
			return 3;
		case "Product Deployment Bootcamp":
			return 5;
		case "Quantitative Research Methods":
			return 2;
		case "Masters Dissertation Part - I":
			return 5;
		case "Masters Dissertation Part - II":
			return 15;
		default :
			return 0;
		}
	}
	
	public static double getMSCAISubjectCredits(String subject) {
		switch (subject) {
		case "Business Communication and Presentation Skills for Data Analytics":
			return 2;
		case "Digital and Social Media Analytics":
			return 2;
		case "Essential Engineering Skills in Data Analytics Using R and Python":
			return 2.5;
		case "Foundations of Probability and Statistics for Data Science":
			return 4;
		case "Hands-on Data Science Project 1":
			return 1.5;
		case "Statistics and Probability in Decision Modeling- 1":
			return 3;
		case "Statistics and Probability in Decision Modeling -2":
			return 3;
		case "The Art and Science of Storytelling with Data Visualizations":
			return 2;
		case "Behavioural Science and Analytics":
			return 2;
		case "Design Thinking":
			return 2;
		case "Methods and Algorithms in Machine Learning -1":
			return 3.5;
		case "Project Management":
			return 2;
		case "AI and Decision Sciences":
			return 3;
		case "Economics for Analysts":
			return 2;
		case "Hands-on Data Science Project 2":
			return 2.5;
		case "Methods and Algorithms in Machine Learning -2":
			return 3;		
		case "Data Structures and Algorithms":
			return 6;
		case "Product Management":
			return 2;
		case "Business Law and Ethics":
			return 2;
		case "Mathematical Analysis for Data Science":
			return 2;
		case "Advanced Mathematical Analysis for Data Science":
			return 2;
		case "Computer vision fundamentals and deep learning applications-1":
			return 4;
		case "Data Engineering-1":
			return 4;
		case "Architecting Enterprise Applications":
			return 2;
		case "Quantitative Research Methods":
			return 2;
		case "Data Engineering-2" :
			return 2;
		case "Computer vision fundamentals and deep learning applications-2":
			return 1;
		case "Text mining and Natural Language Pro-cessing using Deep learning":
			return 5;
		case "Product Deployment Bootcamp":
			return 2;
		case "Master Dissertation":
			return 5;
		case "ML Algorithm Development Bootcamp":
			return 2;
		default :
			return 0;
		}
	}
	
	public static double getMBAWXSubjectCredits(int prgSemSubjectId) {
		if(mbawx4SubjectCreditsPSSIdsList.contains(prgSemSubjectId))
			return 4;
		else if(mbawx8SubjectCreditsPSSIdsList.contains(prgSemSubjectId))
			return 8;
		else if(mbawx3_5SubjectCreditsPSSIdsList.contains(prgSemSubjectId))
			return 3.5;
		
		return 0;
	}
}

