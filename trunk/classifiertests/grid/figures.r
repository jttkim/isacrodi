source("grid.r");

epsdevice <- function(epsFilename)
{
  postscript(epsFilename, width = 8, height = 6, paper = "special", onefile = FALSE, horizontal = FALSE);
}


makeEpsFigures <- function(p)
{
  for (a in c("acc0", "acc1", "acc2"))
  {
    epsdevice(sprintf("svmperformance_mdp_cep_%s.eps", a));
    plotPerformanceLines(p, "mdp", "cep", "nrm", sort(unique(p[["nrm"]]))[3], a, ylim = c(0, 1));
    dev.off();
    epsdevice(sprintf("svmperformance_mdp_nrm_%s.eps", a));
    plotPerformanceLines(p, "mdp", "nrm", "cep", sort(unique(p[["cep"]]))[3], a, ylim = c(0, 1));
    dev.off();
  }
}

