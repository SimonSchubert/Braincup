//
//  CreateSherlockCalculationChallengeView.swift
//  braincup
//
//  Created by Simon Schubert on 26.12.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct CreateRiddleChallengeView: View {
    var title: String
    var description: String
    var back: () -> Void
    
    @State private var inputTitle: String = ""
    @State private var inputSecret: String = ""
    @State private var inputRiddle: String = ""
    @State private var inputAnswers: String = ""
    
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
                EditText(title: "Riddle", helperText: "", onChange: {v in self.inputRiddle = v })
                EditText(title: "Answers", helperText: "Separated by comma.", onChange: {v in self.inputAnswers = v })

                Button(action: {
                    let result = UrlBuilder().buildRiddleChallengeUrl(
                        title: self.inputTitle,
                        secret: self.inputSecret,
                        description: self.inputRiddle,
                        answersInput: self.inputAnswers
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

struct CreateRiddleChallengeView_Previews: PreviewProvider {
    static var previews: some View {
        CreateRiddleChallengeView(title: "", description: "", back: {})
    }
}
