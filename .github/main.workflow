workflow "build" {
  on = "push"
  resolves = [
    "maven",
  ]
}
