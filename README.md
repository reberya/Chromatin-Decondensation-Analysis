Program purpose:
    -Used in conjunction with the 'DANA1' macro within FIJI, this code is designed to analyze NETosis and cellular area for single channel DNA fluoresence microscopy. This program imports .csv files, removes fragments and multiples, and displays information on both the cellular area and NET-like structure characterization in new .csv files. It also generates a summarative file for the sample which details the percent NETosis and average area for all cells within the image.
    
Source code:
    -Multi_NET_Analysis.java: The main class. Responsible for reading in .csv files located within a specified foleder, generating sample-specifc cutoffs, normalzing areas after outlier removal, and exporting a summative .csv file
    -Matrix.java: Responsible for eliminating outliers based on cutoffs, computing averages, and generating image specific .csv file.
  
