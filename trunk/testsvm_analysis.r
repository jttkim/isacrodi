# accuracy is the proportion of CDRs in which the computed diagnosis matches the expert diagnosis.

accuracy <- function(d, cname)
{
  return(sum(as.character(d[["expertDiagnosis"]]) == as.character(d[[cname]])) / nrow(d));
}

# Compute a table of accuracy values for all types of tests (training, "standard", numericOnly etc.).
# Columns missingDescriptorProbability, cauchyRangeMagnifier, categoricalErrorProbability
# are populated from control parameters in attributes if present.
# This function also performs a permutation test to estimate accuracies expected from random guessing.
# This accuracy estimate is stored in the nullAccuracy column. The number of times a null accuracy
# exceeds the accuracy of the computed classification is used to compute a crude p value estimate,
# stored in permPvalue.
accuracyStats <- function(d, numPermutationTests = 100)
{
  s <- data.frame(missingDescriptorProbability = as.numeric(NA), cauchyRangeMagnifier = as.numeric(NA), categoricalErrorProbability = as.numeric(NA), testType = as.character(NA), accuracy0 = as.numeric(NA), nullAccuracy0 = as.numeric(NA), permPvalue0 = as.numeric(NA), accuracy1 = as.numeric(NA), nullAccuracy1 = as.numeric(NA), permPvalue1 = as.numeric(NA), accuracy2 = as.numeric(NA), nullAccuracy2 = as.numeric(NA), permPvalue2 = as.numeric(NA));
  s[["testType"]] <- "";
  controlParameterList <- c("missingDescriptorProbability", "cauchyRangeMagnifier", "categoricalErrorProbability");
  srow <- 1;
  for (testType in as.character(unique(d[["testType"]])))
  {
    aPermSum <- 0.0;
    dtt <- d[d[["testType"]] == testType, ];
    for(cname in colnames(d)[grep("computedDiagnosis", colnames(d))])
    {  
      aReal <- accuracy(dtt, cname);
      n <- 0;
      for (i in 1:numPermutationTests)
      {
        dttPerm <- dtt;
        dttPerm[["computedDiagnosis"]] <- sample(dtt[["computedDiagnosis"]]);
        aPerm <- accuracy(dttPerm, cname);
        aPermSum <- aPermSum + aPerm;
        if (aPerm >= aReal)
        {
          n <- n + 1;
        }
      }
      for (controlParameter in controlParameterList)
      {
        if (!is.null(attr(d, controlParameter)))
        {
          s[srow, controlParameter] <- attr(d, controlParameter);
        }
      }

      s[srow, "testType"] <- testType;
      for(j in (0:2))
      {
        s[srow, sprintf('accuracy%d',j)] <- aReal;
        s[srow, sprintf('nullAccuracy%d',j)] <- aPermSum / numPermutationTests;
        s[srow, sprintf('permPvalue%d',j)] <- n / numPermutationTests;
      # print(sprintf("%s accuracy: %f (%d / %d)", testType, aReal, n, numPermutationTests));
      }
    }
    srow <- srow + 1;
  }
  return(s);
}


# plot histograms of descriptor values by test type
plotHistograms <- function(d, descriptorName, disorderName, binSize = NULL, ...)
{
  d <- d[d[["expertDiagnosis"]] == disorderName, ];
  x <- d[[descriptorName]]
  testTypeList <- as.character(unique(d[["testType"]]));
  minVal <- min(x, na.rm = TRUE);
  maxVal <- max(x, na.rm = TRUE);
  print(sprintf("min = %f, max = %f", minVal, maxVal));
  if (is.null(binSize))
  {
    b <-  0:100 * 0.01 * (maxVal - minVal) + minVal;
  }
  else
  {
    n <- as.integer(ceiling((maxVal - minVal) / binSize));
    b <- 0:n / n * (maxVal - minVal) + minVal;
  }
  opar <- par(no.readonly = TRUE);
  par(mfrow = c(length(testTypeList), 1));
  for (testType in testTypeList)
  {
    xx <- x[d[["testType"]] == testType];
    print(min(xx));
    print(max(xx));
    print(minVal);
    print(maxVal);
    hist(xx, breaks = b, main = testType, xlab = descriptorName, ...);
  }
  par(opar);
}


# read a table of testsvm results from a file following the dummy_mdp<mdp>_crm<crm>_cep<cep>_cdrtable.txt pattern
readDummy <- function(missingDescriptorProbability, cauchyRangeMagnifier, categoricalErrorProbability)
{
  dummyName <- sprintf("dummy_mdp%03d_crm%03d_cep%03d_cdrtable.txt", as.integer(missingDescriptorProbability * 100), as.integer(cauchyRangeMagnifier * 10), as.integer(categoricalErrorProbability * 100));
  d <- read.table(dummyName, header = TRUE, sep = "\t");
  attr(d, "missingDescriptorProbability") <- missingDescriptorProbability;
  attr(d, "cauchyRangeMagnifier") <- cauchyRangeMagnifier;
  attr(d, "categoricalErrorProbability") <- categoricalErrorProbability;
  return(d);
}


# read all testsvm result tables in the current working directory, following the dummy... pattern.
# Returns a list with element names as file names.
readAllDummyCdrtables <- function()
{
  a <- list();
  for (fileName in dir(pattern = "dummy_mdp..._crm..._cep..._cdrtable.txt"))
  {
    missingDescriptorProbability <- as.numeric(substr(fileName, 10, 12)) * 0.01;
    cauchyRangeMagnifier <- as.numeric(substr(fileName, 17, 19)) * 0.1;
    categoricalErrorProbability <- as.numeric(substr(fileName, 24, 26)) * 0.01;
    a[[fileName]] <- readDummy(missingDescriptorProbability, cauchyRangeMagnifier, categoricalErrorProbability);
  }
  return(a);
}


# compute an aggregated accuracy statistics table of all tables in the cdrTableList.
# Note: use the extra arguments for controlling the number of permutation tests.
dummyStatsTable <- function(cdrtableList, ...)
{
  allStats <- NULL;
  for (cdrtable in cdrtableList)
  {
    if (is.null(allStats))
    {
      allStats <- accuracyStats(cdrtable, ...);
    }
    else
    {
      allStats <- rbind(allStats, accuracyStats(cdrtable, ...));
    }
  }
  return(allStats);
}


histogramDemo <- function(d, descriptorName, ...)
{
  for (disorderName in as.character(unique(r[["expertDiagnosis"]])))
  {
    plotHistograms(d, "cropage", disorderName, binSize = 5, ...);
    readline(sprintf("%s -- hit return", disorderName));
  }
}


epsdevice <- function(epsFilename)
{
  postscript(epsFilename, width = 8, height = 6, paper = "special", onefile = FALSE, horizontal = FALSE);
  par(cex = 1.5);
}



accuracyPlotDemo <- function(a)
{
  for(i in (0:2))
  {
    for (testType in as.character(unique(allStats[["testType"]])))
    {
      epsdevice(sprintf("svm_testType_%s_dis%d.eps", testType, j));
      dtmp <- allStats[allStats[["testType"]] == testType, ];
      par(mfrow = c(2, 2));
      for (controlParameter in c("missingDescriptorProbability", "cauchyRangeMagnifier", "categoricalErrorProbability"))
      {
        plot(dtmp[[controlParameter]], dtmp[[sprintf('accuracy%s',j)]], xlab = controlParameter, ylab = "accuracy");
      }
      par(mfrow = c(1, 1));
      dev.off();
      readline(sprintf("%s -- hit return", testType));
    }
  }
}


allTables <- readAllDummyCdrtables();
allStats <- dummyStatsTable(allTables);
# current reference values
#histogramDemo(allTables[["dummy_mdp050_crm005_cep020_cdrtable.txt"]], cropAge, xlim = c(-100,400));
accuracyPlotDemo(allStats);
# 

# d <- read.table("testsvm_hack_results.txt", header = TRUE, sep = "\t");
# printStats(d, "results");
