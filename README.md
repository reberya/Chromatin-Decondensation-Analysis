#DNA Area and NETosis Analysis (DANA)

##Synopsis
DANA_II works in conjunction with DANA_II to quantify NET-like structures and DNA area in fluorescent microscope images. DANA_II automatically imports all of the DANA_I-generated .csv files from a sample, eliminates ROIs corresponding to cell fragments or multiples, and exports a .csv file containing information on the DNA area and NET formation for that sample. 

To accomplish this, DANA_II pools the ROIs from all of the .csv files from the sample and eliminates those with raw integrated densities outside of the user-defined cutoffs. The relative area for the remaining ROIs are then calculated relative to a user-defined area or the areas of the five smallest remaining ROIs. These five smallest ROIs represent condensed nuclei for the sample. Any ROIs with a relative area above a user-defined threshold are labeled as a NET-like structure. DANA_II then exports a .csv file for each image which contains information on the area, relative area compared to condensed nuclei, and NET status for each ROI within that image. This .csv file also contains information on the number and percentage of ROIs above user-defined DNA decondensation cutoff values, the percentage of NETosis, and the average cellular DNA area for that specific image. In addition, for each sample, the program exports a summary .csv file, containing information on the sample’s average percentage NETosis and DNA area.  

##Motivation
Neutrophil extracellular traps (NETs) are a form of cell death in which neutrophils release their decondensed chromatin affixed with antimicrobial molecules. NETosis has been implicated in multiple human diseases and processes including rheumatoid arthritis, thrombosis, pancreatitis, and diabetes. However, the current methods of quantifying NETosis have numerous limitations which create obstacles for studying NETs and NET-like structures in disease. For example, these methods are time-consuming, susceptible to bias, non-specific, require very expensive equipment, and/or are unable to quantify NETosis after neutrophil lysis. To address these issues, we created and validated DANA (DNA Area and NETosis Analysis): a novel ImageJ/Java based program designed to provide a rapid, simple, and objective approach to analyze NETosis and average cellular DNA area.

##Installation
Installation and optimization instructions are availible [here](https://drive.google.com/file/d/0BxasdeBAsMgFZllMelZDT3lPcUk/view?usp=sharing) 

##Tests
