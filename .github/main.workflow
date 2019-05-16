workflow "build" {
  on = "push"
  resolves = [
    "maven",
    "GitHub Action for Maven",
  ]
}

action "GitHub Action for Maven" {
  uses = "LucaFeger/action-maven-cli@master"
  args = "clean install"
}
