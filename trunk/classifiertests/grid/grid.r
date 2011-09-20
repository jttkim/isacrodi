extractControlParameters <- function(fileName)
{
  s <- strsplit(fileName, "_")[[1]];
  mdp <- as.numeric(substring(s[2], 4, 7)) * 0.01;
  nrm <- as.numeric(substring(s[3], 4, 7)) * 0.1;
  cep <- as.numeric(substring(s[4], 4, 7)) * 0.01;
  return(data.frame(mdp = mdp, nrm = nrm, cep = cep));
}


findPerformanceStats <- function(fileName)
{
  pStats <- extractControlParameters(fileName);
  d <- read.table(fileName, header = TRUE, sep = "\t", na.strings = "");
  b <- as.character(d[["expertDiagnosis"]]) == as.character(d[["computedDiagnosis0"]]);
  scoreDiff <- d[["diagnosisScore0"]] - d[["diagnosisScore1"]];
  pStats[["msdTrue"]] <- mean(scoreDiff[b]);
  pStats[["msdFalse"]] <- mean(scoreDiff[!b]);
  pStats[["infTrue"]] <- mean(d[["information"]][b]);
  pStats[["infFalse"]] <- mean(d[["information"]][!b]);
  pStats[["acc0"]] <- sum(b) / nrow(d);
  b <- b | (as.character(d[["expertDiagnosis"]]) == as.character(d[["computedDiagnosis1"]]));
  pStats[["acc1"]] <- sum(b) / nrow(d);
  b <- b | (as.character(d[["expertDiagnosis"]]) == as.character(d[["computedDiagnosis2"]]));
  pStats[["acc2"]] <- sum(b) / nrow(d);
  for (n in colnames(d)[9:ncol(d)])
  {
    pStats[[sprintf("miss.%s", n)]] <- sum(is.na(d[[n]])) / nrow(d);
  }
  return(pStats);
}


readPerformanceStats <- function(prefix)
{
  psTable <- NULL;
  for (fileName in dir(pattern = sprintf("%s_mdp..._nrm..._cep..._cdrtable.txt", prefix)))
  {
    p <- findPerformanceStats(fileName)
    if (is.null(psTable))
    {
      psTable <- p;
    }
    else
    {
      psTable <- rbind(psTable, p);
    }
    print(sprintf("%s: done", fileName));
    print(gc());
  }
  return(psTable);
}


plotPerformance3D <- function(psTable, xlab, ylab, zlab, zval, ...)
{
  p <- psTable[psTable[[zlab]] == zval, ];
  o <- order(p[[ylab]], p[[xlab]]);
  x <- sort(unique(p[[xlab]]));
  y <- sort(unique(p[[ylab]]));
  z <- matrix(p[["acc0"]][o], nrow = length(x), ncol = length(y), byrow = FALSE);
  persp(x, y, z, xlab = xlab, ylab = ylab, zlab = "accuracy", ...);
}


plotPerformance2D <- function(psTable, xlab, ylab, zlab, zval, ...)
{
  p <- psTable[psTable[[zlab]] == zval, ];
  o <- order(p[[ylab]], p[[xlab]]);
  x <- sort(unique(p[[xlab]]));
  y <- sort(unique(p[[ylab]]));
  z <- matrix(p[["acc0"]][o], nrow = length(x), ncol = length(y), byrow = FALSE);
  image(x, y, z, xlab = xlab, ylab = ylab, sub = sprintf("%s = %f", zlab, zval), ...);
}


plotPerformanceLines <- function(psTable, xlab, ylab, zlab, zval, acclab, ...)
{
  p <- psTable[psTable[[zlab]] == zval, ];
  o <- order(p[[ylab]], p[[xlab]]);
  x <- sort(unique(p[[xlab]]));
  y <- sort(unique(p[[ylab]]));
  z <- matrix(p[[acclab]][o], nrow = length(x), ncol = length(y), byrow = FALSE);
  colList <- heat.colors(length(y));
  j <- 1;
  plot(x, z[, j], type = "l", col = colList[j], xlab = xlab, ylab = "accuracy", sub = sprintf("%s = %1.1f", zlab, zval), ...);
  for (j in 2:length(y))
  {
    lines(x, z[, j], col = colList[j]);
  }
  legend("topright", col = colList, legend = y, lwd = 1, title = ylab);
}


readGridTable <- function(prefix)
{
  gridTable <- NULL;
  for (fileName in dir(pattern = sprintf("%s_mdp..._nrm..._cep..._cdrtable.txt", prefix)))
  {
    s <- strsplit(fileName, "_")[[1]];
    mdp <- as.numeric(substring(s[2], 4, 7)) * 0.01;
    nrm <- as.numeric(substring(s[3], 4, 7)) * 0.1;
    cep <- as.numeric(substring(s[4], 4, 7)) * 0.01;
    d <- read.table(fileName, header = TRUE, sep = "\t", na.strings = "");
    d[["mdp"]] <- rep(mdp, nrow(d));
    d[["nrm"]] <- rep(nrm, nrow(d));
    d[["cep"]] <- rep(cep, nrow(d));
    d <- d[, c("mdp", "nrm", "cep", "expertDiagnosis", "computedDiagnosis0", "diagnosisScore0", "computedDiagnosis1", "diagnosisScore1", "computedDiagnosis2", "diagnosisScore2")];
    if (is.null(gridTable))
    {
      gridTable <- d;
    }
    else
    {
      gridTable <- rbind(gridTable, d);
    }
    print(sprintf("%s: done", fileName));
    print(gc());
  }
  return(gridTable);
}
