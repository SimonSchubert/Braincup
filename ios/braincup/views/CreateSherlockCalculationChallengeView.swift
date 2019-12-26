//
//  CreateSherlockCalculationChallengeView.swift
//  braincup
//
//  Created by Simon Schubert on 26.12.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct CreateSherlockCalculationChallengeView: View {
    var title: String
    var description: String
    var back: () -> Void
    
    @State private var inputTitle: String = ""
    @State private var inputSecret: String = ""
    @State private var inputGoal: String = ""
    @State private var inputNumbers: String = ""
    
    @State private var showingAlert = false
    @State private var isSharePresented: Bool = false
    
    @State private var url = ""
    @State private var errorMessage = ""
    
    var body: some View {
        NavigationView {
            ScrollView {
            VStack {
                Text("Create challenge").font(.title).padding(.horizontal, 16)
                            
                EditText(title: "Title", helperText: "Title of the challenge. (optional)", onChange: {v in self.inputTitle = v })
                EditText(title: "Secret", helperText: "The secret will be revealed after solving the challenge. (optional)", onChange: {v in self.inputSecret = v })
                EditText(title: "Goal", helperText: "The goal that has to be found.", onChange: {v in self.inputGoal = v })
                EditText(title: "Allowed numbers", helperText: "The allowed numbers to find the goal. (Separated by comma or space)", onChange: {v in self.inputNumbers = v })

                Button(action: {
                    let result = UrlBuilder().buildSherlockCalculationChallengeUrl(
                        title: self.inputTitle,
                        secret: self.inputSecret,
                        goalInput: self.inputGoal,
                        numbersInput: self.inputNumbers
                    )
                    if(result is ChallengeUrl) {
                        self.url = (result as! ChallengeUrl).url
                        self.showingAlert = false
                        self.isSharePresented = true
                    } else if(result is ChallengeUrlError) {
                        self.errorMessage = (result as! ChallengeUrlError).errorMessage
                        self.showingAlert = true
                    }
                }) {
                    Text("Create")
                }.buttonStyle(BackgroundButtonStyle()).padding(.top, 32).alert(isPresented: $showingAlert) {
                    Alert(title: Text("Error"), message: Text(errorMessage), dismissButton: .default(Text("Ok")))
                }.sheet(isPresented: $isSharePresented, content: {
                    ActivityViewController(activityItems: [URL(string: self.url)!])
                })
                
                }.frame(minWidth: 0, maxWidth: 300)
            .navigationBarItems(leading: Button(action: { self.back()}){Image("back").foregroundColor(Color(hex: 0xFFED7354))})
            }
        }.navigationViewStyle(StackNavigationViewStyle())
    }
}

struct CreateSherlockCalculationChallengeView_Previews: PreviewProvider {
    static var previews: some View {
        CreateSherlockCalculationChallengeView(title: "", description: "", back: {})
    }
}
